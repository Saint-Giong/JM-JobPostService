package rmit.saintgiong.jobpost.domain.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpost.api.internal.DeleteJobPostInterface;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.validators.JobPostBaseValidator;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class JobPostDeleteService implements DeleteJobPostInterface {

    private final JobPostRepository repository;

    private final JobPostBaseValidator<Void> baseValidator;

    @Override
    public void deleteJobPost(String id) {
        log.info("method=deleteJobPost, message=Start deleting job post, id={}", id);

        UUID uuid = UUID.fromString(id);

        JobPostEntity existing = baseValidator.assertExistsById(uuid);

        repository.delete(existing);

        log.info("method=deleteJobPost, message=Successfully deleted job post, id={}", id);
    }
}
