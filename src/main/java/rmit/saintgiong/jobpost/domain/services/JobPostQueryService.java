package rmit.saintgiong.jobpost.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpost.api.internal.QueryJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpost.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.validators.JobPostBaseValidator;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobPostQueryService implements QueryJobPostInterface {

    private final JobPostMapper mapper;

    private final JobPostBaseValidator<Void> baseValidator;

    private final JobPostRepository repository;

    public JobPostQueryService(JobPostMapper mapper, JobPostBaseValidator<Void> baseValidator, JobPostRepository repository) {
        this.mapper = mapper;
        this.baseValidator = baseValidator;
        this.repository = repository;
    }

    @Override
    public QueryJobPostResponseDto getJobPostById(String id) {
        log.info("method=getJobPostById, message=Start fetching job post details, id={}", id);

        UUID uuid = UUID.fromString(id);

        JobPostEntity existing = baseValidator.assertExistsById(uuid);

        QueryJobPostResponseDto response = mapper.toQueryResponse(existing);

        log.info("method=getJobPostById, message=Successfully fetched job post details, id={}", id);

        return response;
    }

    @Override
    public List<QueryJobPostResponseDto> getJobPostsByCompanyId(String companyId) {
        log.info("method=getJobPostsByCompanyId, message=Start fetching job posts for company, companyId={}", companyId);

        UUID companyUuid = UUID.fromString(companyId);

        // Ensure company exists (placeholder logic in validator)
        baseValidator.assertCompanyExists(companyUuid);

        List<JobPostEntity> entities = repository.findAllByCompanyId(companyUuid);

        List<QueryJobPostResponseDto> response = entities.stream()
                .map(mapper::toQueryResponse)
                .collect(Collectors.toList());

        log.info("method=getJobPostsByCompanyId, message=Successfully fetched {} job posts for company={}, companyId={}", response.size(), companyUuid, companyId);

        return response;
    }
}
