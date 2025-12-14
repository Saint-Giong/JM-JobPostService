package rmit.saintgiong.jobpost.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.domain.models.JobPost;

@Mapper(componentModel = "spring")
public interface JobPostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    JobPost fromCreateCommand(CreateJobPostRequestDto dto);
}
