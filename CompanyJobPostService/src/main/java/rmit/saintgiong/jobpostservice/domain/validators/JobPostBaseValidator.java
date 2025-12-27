package rmit.saintgiong.jobpostservice.domain.validators;

import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpostservice.common.exception.DomainException;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;

import java.util.UUID;

import static rmit.saintgiong.jobpostservice.common.exception.DomainCode.RESOURCE_NOT_FOUND;

@Component
public class JobPostBaseValidator<T> extends BaseValidator<T> {

    protected final JobPostRepository repository;

    protected JobPostBaseValidator(JobPostRepository repository) {
        this.repository = repository;
    }


    public void assertCompanyExists(UUID companyId) {
        // TODO: Implement actual company existence check
        boolean companyExists = true;

        if (!companyExists) {
            throw new DomainException(RESOURCE_NOT_FOUND,
                    "Company with ID '" + companyId + "' does not exist");
        }
    }

    public JobPostEntity assertExistsById(UUID id) {
        return repository.findById(id).orElseThrow(() ->
                new DomainException(RESOURCE_NOT_FOUND,
                        "Job post with ID '" + id + "' does not exist"));
    }

    @Override
    public void validate(T target) {
    }
}
