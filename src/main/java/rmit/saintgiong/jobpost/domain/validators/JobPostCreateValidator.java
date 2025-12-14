package rmit.saintgiong.jobpost.domain.validators;

import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;

import java.util.UUID;

@Component
public class JobPostCreateValidator extends JobPostBaseValidator<CreateJobPostRequestDto> {

    protected JobPostCreateValidator(JobPostRepository repository) {
        super(repository);
    }

    @Override
    public void validate(CreateJobPostRequestDto dto) {
        errors.clear();

        assertCompanyExists(UUID.fromString(dto.getCompanyId()));

        throwIfErrors();
    }
}
