package rmit.saintgiong.jobpostservice.domain.services.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpostapi.external.services.ExternalJobPostRequestInterface;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.ApplicationResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryCompanyProfileResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpostapi.internal.services.QueryJobPostInterface;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.services.external.ExternalApplicationService;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostBaseValidator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobPostQueryService implements QueryJobPostInterface {

    private final JobPostMapper mapper;

    private final JobPostBaseValidator<Void> baseValidator;

    private final JobPostRepository repository;

    private final ExternalJobPostRequestInterface externalJobPostRequestInterface;

    private final ExternalApplicationService externalApplicationService;

    public JobPostQueryService(JobPostMapper mapper, JobPostBaseValidator<Void> baseValidator,
            JobPostRepository repository, ExternalJobPostRequestInterface externalJobPostRequestInterface,
            ExternalApplicationService externalApplicationService) {
        this.mapper = mapper;
        this.baseValidator = baseValidator;
        this.repository = repository;
        this.externalJobPostRequestInterface = externalJobPostRequestInterface;
        this.externalApplicationService = externalApplicationService;
    }

    @Override
    public QueryJobPostResponseDto getJobPostById(String id) {
        log.info("method=getJobPostById, message=Start fetching job post details, id={}", id);

        UUID uuid = UUID.fromString(id);

        JobPostEntity existing = baseValidator.assertExistsById(uuid);

        QueryCompanyProfileResponseDto responseDto = externalJobPostRequestInterface
                .sendGetProfileRequest(existing.getCompanyId());
        if (responseDto.getId() == null) {
            log.warn("Failed get profile for ID: {}", id);
            return mapper.toQueryResponse(existing);
        }
        log.info("Successfully create profile for ID: {}", id);

        // Fetch application count
        Integer applicationCount = externalApplicationService.getApplicationCount(id);
        QueryJobPostResponseDto response = mapper.toQueryResponse(existing, applicationCount);
        response.setCompany(responseDto);
        response.setCompanyId(existing.getCompanyId().toString());

        log.info("method=getJobPostById, message=Successfully fetched job post details, id={}", id);

        return response;
    }

    @Override
    public List<QueryJobPostResponseDto> getJobPostsByCompanyId(String companyId) {
        log.info("method=getJobPostsByCompanyId, message=Start fetching job posts for company, companyId={}",
                companyId);

        UUID companyUuid = UUID.fromString(companyId);

        baseValidator.assertCompanyExists(companyUuid);

        List<JobPostEntity> entities = repository.findAllByCompanyId(companyUuid);

        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        QueryCompanyProfileResponseDto responseDto = externalJobPostRequestInterface.sendGetProfileRequest(companyUuid);
        if (responseDto.getId() == null) {
            log.warn("Failed get profile for ID: {}", companyUuid);
            return entities.stream()
                    .map(mapper::toQueryResponse)
                    .collect(Collectors.toList());
        }

        log.info("Successfully create profile for ID: {}", companyUuid);

        // 4. Map entities and attach the SAME profile to all
        List<QueryJobPostResponseDto> response = entities.stream()
                .map(entity -> {
                    // Fetch application count for each post
                    Integer applicationCount = externalApplicationService
                            .getApplicationCount(entity.getId().toString());
                    QueryJobPostResponseDto dto = mapper.toQueryResponse(entity, applicationCount);
                    dto.setCompany(responseDto);
                    dto.setCompanyId(entity.getCompanyId().toString());
                    return dto;
                })
                .collect(Collectors.toList());

        log.info(
                "method=getJobPostsByCompanyId, message=Successfully fetched {} job posts for company={}, companyId={}",
                response.size(), companyUuid, companyId);

        return response;
    }

    // TEST ENDPOINT
    @Override
    public List<QueryJobPostResponseDto> getAllJobPosts() {
        log.info("method=getAllJobPosts, message=Start fetching all job posts");

        // 1. Fetch all posts from DB
        List<JobPostEntity> entities = repository.findAll();

        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Fetch ALL Profiles via Kafka (Inlined Logic)
        // We initialize an empty map so if the call fails, we just return job posts
        // without profiles.
        Map<String, QueryCompanyProfileResponseDto> profileMap = new HashMap<>();

        try {
            // Call the new interface method that uses 'GetAllProfilesRequestRecord'
            List<QueryCompanyProfileResponseDto> allProfiles = externalJobPostRequestInterface
                    .sendGetAllProfilesRequest();

            if (allProfiles != null && !allProfiles.isEmpty()) {
                // Convert List -> Map for O(1) lookup
                profileMap = allProfiles.stream()
                        .filter(p -> p != null && p.getId() != null)
                        .collect(Collectors.toMap(
                                QueryCompanyProfileResponseDto::getId, // Key: UUID String
                                Function.identity(), // Value: The DTO itself
                                (existing, replacement) -> existing));
            }
        } catch (Exception e) {
            // Log the error but DO NOT throw it. We still want to return the Job Posts.
            log.error("Failed to fetch all profiles via Kafka. Returning job posts without profile data.", e);
        }

        // 3. Map entities to DTOs and attach the correct profile
        Map<String, QueryCompanyProfileResponseDto> finalProfileMap = profileMap; // Variable for lambda usage

        List<QueryJobPostResponseDto> response = entities.stream()
                .map(entity -> {
                    // Fetch application count for all posts
                    Integer applicationCount = externalApplicationService
                            .getApplicationCount(entity.getId().toString());
                    QueryJobPostResponseDto dto = mapper.toQueryResponse(entity, applicationCount);

                    if (entity.getCompanyId() != null) {
                        String companyIdStr = entity.getCompanyId().toString();

                        // Fast lookup from our pre-fetched map
                        QueryCompanyProfileResponseDto profile = finalProfileMap.get(companyIdStr);

                        if (profile != null) {
                            dto.setCompany(profile);
                        }
                        dto.setCompanyId(companyIdStr);
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        log.info("method=getAllJobPosts, message=Successfully fetched {} job posts", response.size());
        return response;
    }

    @Override
    public List<ApplicationResponseDto> getApplicationsByJobPostId(String postId) {
        log.info("method=getApplicationsByJobPostId, message=Start fetching applications for postId={}", postId);

        // Validate job post exists
        UUID uuid = UUID.fromString(postId);
        baseValidator.assertExistsById(uuid);

        // Fetch applications from external service
        List<ApplicationResponseDto> applications = externalApplicationService.getApplicationsByPostId(postId);

        log.info("method=getApplicationsByJobPostId, message=Successfully fetched {} applicants for postId={}",
                applications.size(), postId);

        return applications;
    }
}
