package rmit.saintgiong.jobpostservice.domain.services.internal;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import rmit.saintgiong.jobpostapi.external.services.ExternalJobPostRequestInterface;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryCompanyProfileResponseDto;
import rmit.saintgiong.jobpostapi.internal.services.UpdateJobPostInterface;
import rmit.saintgiong.jobpostservice.common.exception.domain.DomainException;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostUpdateValidator;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.shared.dto.avro.jobpost.GetProfileResponseRecord;
import rmit.saintgiong.shared.dto.avro.jobpost.JobPostUpdateSentRecord;
import rmit.saintgiong.shared.type.KafkaTopic;

import java.time.ZoneOffset;
import java.util.ArrayList;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static rmit.saintgiong.jobpostapi.internal.common.type.DomainCode.RESOURCE_NOT_FOUND;


@Service
@Slf4j
public class JobPostUpdateService implements UpdateJobPostInterface {

    private final JobPostMapper jobPostMapper;
    private final JobPostRepository repository;
    private final JobPostUpdateValidator updateValidator;

    private final KafkaTemplate<String, Object> cloudKafkaTemplate;
    private final ExternalJobPostRequestInterface externalJobPostRequestInterface;

    public JobPostUpdateService(JobPostMapper jobPostMapper, JobPostRepository repository, JobPostUpdateValidator updateValidator, KafkaTemplate<String, Object> cloudKafkaTemplate, ExternalJobPostRequestInterface externalJobPostRequestInterface) {
        this.jobPostMapper = jobPostMapper;
        this.repository = repository;
        this.updateValidator = updateValidator;
        this.cloudKafkaTemplate = cloudKafkaTemplate;
        this.externalJobPostRequestInterface = externalJobPostRequestInterface;
    }

    @Override
    @Transactional
    public void updateJobPost(String id, UpdateJobPostRequestDto requestDto) {
        log.info("method=updateJobPost, message=Start updating job post, id={}, param={}", id, requestDto);

        UUID uuid = UUID.fromString(id);

        // Validate
        updateValidator.validate(requestDto);

        // Find existing job post
        JobPostEntity existing = repository.findById(uuid).orElseThrow(() ->
                new DomainException(RESOURCE_NOT_FOUND, "Job post with ID '" + id + "' does not exist"));

        JobPostEntity updatedEntity = jobPostMapper.fromUpdateCommand(requestDto);
        updatedEntity.setId(existing.getId());
        updatedEntity.setPostedDate(existing.getPostedDate());

        // Handle skill tags
        if (requestDto.getSkillTagIds() != null) {
            requestDto.getSkillTagIds().forEach(updatedEntity::addSkillTag);
        }

        JobPostEntity saved = repository.saveAndFlush(updatedEntity);

        QueryCompanyProfileResponseDto responseDto = externalJobPostRequestInterface.sendGetProfileRequest(saved.getCompanyId());
        if (responseDto.getId() == null) {
            log.warn("Failed get profile for ID: {}", saved.getCompanyId());
        }
        log.info("Successfully create profile for ID: {}", saved.getCompanyId());

        GetProfileResponseRecord queryCompanyProfileResponseDto =  GetProfileResponseRecord.newBuilder()
                .setId(UUID.fromString(responseDto.getId()))
                .setName(responseDto.getName())
                .setCountry(responseDto.getCountry())
                .setPhone(responseDto.getPhone())
                .setAddress(responseDto.getAddress())
                .setCity(responseDto.getCity())
                .setAboutUs(responseDto.getAboutUs())
                .setAdmissionDescription(responseDto.getAdmissionDescription())
                .setLogoUrl(responseDto.getLogoUrl())
                .build();


        JobPostUpdateSentRecord jobPostUpdateSentRecord = JobPostUpdateSentRecord.newBuilder()
                .setId(saved.getId())
                .setTitle(saved.getTitle())
                .setDescription(saved.getDescription())
                .setCity(saved.getCity())
                .setEmploymentType(new ArrayList<>(jobPostMapper.mapBitSetToStrings(saved.getEmploymentType())))
                .setSalaryTitle(String.valueOf(saved.getSalaryTitle()))
                .setSalaryMin(saved.getSalaryMin())
                .setSalaryMax(saved.getSalaryMax())
                .setPostedDate(saved.getPostedDate().toInstant(ZoneOffset.UTC))
                .setExpiryDate(saved.getExpiryDate().toInstant(ZoneOffset.UTC))
                .setPublished(saved.isPublished())
                .setCountry(saved.getCountry())
                .setCompanyId(saved.getCompanyId())
                .setSkillTagIds(saved.getSkillTags() != null
                        ? saved.getSkillTags().stream()
                            .map(tag -> tag.getSkillTagId().getTagId())
                            .collect(java.util.stream.Collectors.toList())
                        : java.util.Collections.emptyList())
                .setCompany(queryCompanyProfileResponseDto)
                .build();

        ProducerRecord<String, Object> kafkaRequest = new ProducerRecord<>(
                KafkaTopic.JM_POST_UPDATED_TOPIC,
                jobPostUpdateSentRecord
        );

        try {
            cloudKafkaTemplate.send(kafkaRequest);
        } catch (Exception e) {
            log.error("Error while sending");
        }

        log.info("method=updateJobPost, message=Successfully updated job post, id={}", saved.getId());
    }
}
