package rmit.saintgiong.jobpost.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpost.domain.models.JobPost;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;

@Mapper(componentModel = "spring")
public interface JobPostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    JobPost fromCreateCommand(CreateJobPostRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    JobPost fromUpdateCommand(UpdateJobPostRequestDto dto);

    JobPostEntity toEntity(JobPost model);

    QueryJobPostResponseDto toQueryResponse(JobPostEntity entity);
}
