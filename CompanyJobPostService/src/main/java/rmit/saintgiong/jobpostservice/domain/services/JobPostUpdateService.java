package rmit.saintgiong.jobpostservice.domain.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.services.UpdateJobPostInterface;
import rmit.saintgiong.jobpostservice.common.exception.DomainException;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpostservice.domain.models.JobPost;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostUpdateValidator;

import java.util.UUID;

import static rmit.saintgiong.jobpostservice.common.exception.DomainCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
@Slf4j
public class JobPostUpdateService implements UpdateJobPostInterface {

    private final JobPostMapper mapper;
    private final JobPostRepository repository;
    private final JobPostUpdateValidator updateValidator;

    @Override
    @Transactional
    public void updateJobPost(String id, UpdateJobPostRequestDto requestDto) {
        log.info("method=updateJobPost, message=Start updating job post, id={}, param={}", id, requestDto);

        UUID uuid = UUID.fromString(id);

        // Validate
        updateValidator.validate(requestDto);

        // Find existing job post
        JobPostEntity existing = repository.findById(uuid).orElseThrow(() ->
                new DomainException(RESOURCE_NOT_FOUND, "Job post with ID '" + id + "' does not exist"));

        JobPost updatedModel = mapper.fromUpdateCommand(requestDto);
        updatedModel.setId(existing.getId());

        JobPostEntity updatedEntity = mapper.toEntity(updatedModel);
        JobPostEntity saved = repository.save(updatedEntity);

        log.info("method=updateJobPost, message=Successfully updated job post, id={}", saved.getId());
    }
}
