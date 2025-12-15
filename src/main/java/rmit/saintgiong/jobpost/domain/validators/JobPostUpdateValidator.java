package rmit.saintgiong.jobpost.domain.validators;

import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpost.api.internal.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;

import java.util.UUID;

@Component
public class JobPostUpdateValidator extends JobPostBaseValidator<UpdateJobPostRequestDto> {

    public JobPostUpdateValidator(JobPostRepository repository) {
        super(repository);
    }

    public void validate(UpdateJobPostRequestDto dto) {
        errors.clear();

        assertCompanyExists(UUID.fromString(dto.getCompanyId()));

        throwIfErrors();
    }
}
