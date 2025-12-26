package rmit.saintgiong.jobpost.domain.mappers;

import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPost_SkillTagEntity;
import rmit.saintgiong.jobpost.api.internal.type.EmploymentTypeEnum;

import java.util.BitSet;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JobPostMapper {

    public BitSet mapStringsToBitSet(Set<String> types) {
        if (types == null || types.isEmpty()) {
            return new BitSet();
        }
        BitSet bitSet = new BitSet();
        for (String type : types) {
            try {
                int index = EmploymentTypeEnum.getIndexByName(type);
                bitSet.set(index);
            } catch (IllegalArgumentException e) {
                // Ignore invalid enums or throw exception
            }
        }
        return bitSet;
    }

    public Set<String> mapBitSetToStrings(BitSet bitSet) {
        if (bitSet == null || bitSet.isEmpty()) {
            return Collections.emptySet();
        }
        return bitSet.stream()
                .mapToObj(index -> {
                    for (EmploymentTypeEnum e : EmploymentTypeEnum.values()) {
                        if (e.getBitIndex() == index) {
                            return e.name();
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<Integer> mapSkillTagsToIds(Set<JobPost_SkillTagEntity> skillTags) {
        if (skillTags == null) {
            return Collections.emptySet();
        }
        return skillTags.stream()
                .map(entity -> entity.getSkillTagId().getTagId())
                .collect(Collectors.toSet());
    }

    public JobPostEntity fromCreateCommand(CreateJobPostRequestDto dto) {
        if (dto == null) {
            return null;
        }

        JobPostEntity.JobPostEntityBuilder builder = JobPostEntity.builder();

        builder.title(dto.getTitle());
        builder.description(dto.getDescription());
        builder.city(dto.getCity());
        builder.employmentType(mapStringsToBitSet(dto.getEmploymentTypes()));
        builder.salaryTitle(dto.getSalaryTitle());
        builder.salaryMin(dto.getSalaryMin());
        builder.salaryMax(dto.getSalaryMax());
        builder.expiryDate(dto.getExpiryDate());
        builder.published(dto.isPublished());
        builder.country(dto.getCountry());

        if (dto.getCompanyId() != null) {
            builder.companyId(UUID.fromString(dto.getCompanyId()));
        }

        return builder.build();
    }

    public JobPostEntity fromUpdateCommand(UpdateJobPostRequestDto dto) {
        if (dto == null) {
            return null;
        }

        JobPostEntity.JobPostEntityBuilder builder = JobPostEntity.builder();

        builder.title(dto.getTitle());
        builder.description(dto.getDescription());
        builder.city(dto.getCity());
        builder.employmentType(mapStringsToBitSet(dto.getEmploymentTypes()));
        builder.salaryTitle(dto.getSalaryTitle());
        builder.salaryMin(dto.getSalaryMin());
        builder.salaryMax(dto.getSalaryMax());
        builder.expiryDate(dto.getExpiryDate());
        builder.published(dto.isPublished());
        builder.country(dto.getCountry());

        if (dto.getCompanyId() != null) {
            builder.companyId(UUID.fromString(dto.getCompanyId()));
        }

        return builder.build();
    }

    public QueryJobPostResponseDto toQueryResponse(JobPostEntity entity) {
        if (entity == null) {
            return null;
        }

        QueryJobPostResponseDto.QueryJobPostResponseDtoBuilder builder = QueryJobPostResponseDto.builder();

        if (entity.getId() != null) {
            builder.id(entity.getId().toString());
        }
        builder.title(entity.getTitle());
        builder.description(entity.getDescription());
        builder.city(entity.getCity());
        builder.employmentTypes(mapBitSetToStrings(entity.getEmploymentType()));
        builder.salaryTitle(entity.getSalaryTitle());
        builder.salaryMin(entity.getSalaryMin());
        builder.salaryMax(entity.getSalaryMax());
        builder.postedDate(entity.getPostedDate());
        builder.expiryDate(entity.getExpiryDate());
        builder.published(entity.isPublished());
        builder.country(entity.getCountry());
        
        if (entity.getCompanyId() != null) {
            builder.companyId(entity.getCompanyId().toString());
        }
        
        builder.skillTagIds(mapSkillTagsToIds(entity.getSkillTags()));

        return builder.build();
    }
}
