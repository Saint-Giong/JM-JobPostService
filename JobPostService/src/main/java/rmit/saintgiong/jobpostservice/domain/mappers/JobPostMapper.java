package rmit.saintgiong.jobpostservice.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpostservice.domain.models.JobPost;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;

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
