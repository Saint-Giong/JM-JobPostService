package rmit.saintgiong.jobpost.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
import java.util.stream.Collectors;

@Mapper(componentModel = "spring" )
public abstract class JobPostMapper {

    @Named("mapStringsToBitSet")
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

    @Named("mapBitSetToStrings")
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

    @Named("mapSkillTagsToIds")
    public Set<Integer> mapSkillTagsToIds(Set<JobPost_SkillTagEntity> skillTags) {
        if (skillTags == null) {
            return Collections.emptySet();
        }
        return skillTags.stream()
                .map(entity -> entity.getSkillTagId().getTagId())
                .collect(Collectors.toSet());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    @Mapping(target = "skillTags", ignore = true)
    @Mapping(target = "employmentType", source = "employmentTypes", qualifiedByName = "mapStringsToBitSet")
    public abstract JobPostEntity fromCreateCommand(CreateJobPostRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    @Mapping(target = "skillTags", ignore = true)
    @Mapping(target = "employmentType", source = "employmentTypes", qualifiedByName = "mapStringsToBitSet")
    public abstract JobPostEntity fromUpdateCommand(UpdateJobPostRequestDto dto);


    @Mapping(target = "employmentTypes", source = "employmentType", qualifiedByName = "mapBitSetToStrings")
    @Mapping(target = "skillTagIds", source = "skillTags", qualifiedByName = "mapSkillTagsToIds")
    public abstract QueryJobPostResponseDto toQueryResponse(JobPostEntity entity);




}
