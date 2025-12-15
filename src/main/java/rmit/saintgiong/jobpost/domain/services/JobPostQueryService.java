package rmit.saintgiong.jobpost.domain.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpost.api.internal.QueryJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpost.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.validators.JobPostBaseValidator;

import java.util.UUID;;

@Service
@AllArgsConstructor
@Slf4j
public class JobPostQueryService implements QueryJobPostInterface {

    private final JobPostMapper mapper;

    private final JobPostBaseValidator<Void> baseValidator;

    @Override
    public QueryJobPostResponseDto getJobPostById(String id) {
        log.info("method=getJobPostById, message=Start fetching job post details, id={}", id);

        UUID uuid = UUID.fromString(id);

        JobPostEntity existing = baseValidator.assertExistsById(uuid);

        QueryJobPostResponseDto response = mapper.toQueryResponse(existing);

        log.info("method=getJobPostById, message=Successfully fetched job post details, id={}", id);

        return response;
    }
}
