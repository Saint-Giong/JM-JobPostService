package rmit.saintgiong.jobpostservice.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpostapi.internal.services.DeleteJobPostInterface;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostBaseValidator;

import java.util.UUID;

@Service
@Slf4j
public class JobPostDeleteService implements DeleteJobPostInterface {

    private final JobPostRepository repository;

    private final JobPostBaseValidator<Void> baseValidator;

    public JobPostDeleteService(JobPostRepository repository, JobPostBaseValidator<Void> baseValidator) {
        this.repository = repository;
        this.baseValidator = baseValidator;
    }

    @Override
    public void deleteJobPost(String id) {
        log.info("method=deleteJobPost, message=Start deleting job post, id={}", id);

        UUID uuid = UUID.fromString(id);

        JobPostEntity existing = baseValidator.assertExistsById(uuid);

        repository.delete(existing);

        log.info("method=deleteJobPost, message=Successfully deleted job post, id={}", id);
    }
}
