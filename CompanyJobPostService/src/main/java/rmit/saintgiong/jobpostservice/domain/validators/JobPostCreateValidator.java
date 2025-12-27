package rmit.saintgiong.jobpostservice.domain.validators;

import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;

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
