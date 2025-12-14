package rmit.saintgiong.jobpost.domain.validators;

import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpost.common.exception.DomainCode;
import rmit.saintgiong.jobpost.common.exception.DomainException;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;

import java.util.Optional;
import java.util.UUID;

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
            throw new DomainException(DomainCode.RESOURCE_NOT_FOUND,
                    "Company with ID '" + companyId + "' does not exist");
        }
    }

    public JobPostEntity assertExistsById(UUID id) {
        return repository.findById(id).orElseThrow(() ->
                new DomainException(DomainCode.RESOURCE_NOT_FOUND,
                        "Job post with ID '" + id + "' does not exist"));
    }

    @Override
    public void validate(T target) {
    }
}
