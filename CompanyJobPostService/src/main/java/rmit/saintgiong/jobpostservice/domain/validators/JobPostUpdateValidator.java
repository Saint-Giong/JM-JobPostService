package rmit.saintgiong.jobpostservice.domain.validators;

import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;

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
