package rmit.saintgiong.jobpost.domain.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpost.api.internal.CreateJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.response.CreateJobPostResponseDto;
import rmit.saintgiong.jobpost.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.validators.JobPostCreateValidator;

@Service
@AllArgsConstructor
@Slf4j
public class JobPostCreateService implements CreateJobPostInterface {

    private final JobPostMapper mapper;

    private final JobPostRepository repository;

    private final JobPostCreateValidator createValidator;

    @Override
    @Transactional
    public CreateJobPostResponseDto createJobPost(CreateJobPostRequestDto request) {
        log.info("method=createJobPost, message=Start creating job post, param={}", request);

        // Validate business rules
        createValidator.validate(request);

        // Map from create request DTO to domain model
        var newJob = mapper.fromCreateCommand(request);

        // Map from domain model to persistence entity
        JobPostEntity entity = mapper.toEntity(newJob);
        log.info("method=createJobPost, message=Mapped job post entity, entity={}", entity);

        // Persist the new job post
        JobPostEntity saved = repository.save(entity);

        log.info("method=createJobPost, message=Successfully created job post, id={}", saved.getId());

        // Build and return the response DTO
        return CreateJobPostResponseDto.builder()
                .id(String.valueOf(saved.getId()))
                .build();
    }
}
