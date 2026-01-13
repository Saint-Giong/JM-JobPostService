package rmit.saintgiong.jobpostservice.domain.services.internal;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import rmit.saintgiong.jobpostapi.external.services.ExternalJobPostRequestInterface;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.CreateJobPostResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryCompanyProfileResponseDto;
import rmit.saintgiong.jobpostapi.internal.services.CreateJobPostInterface;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostCreateValidator;
import rmit.saintgiong.shared.dto.avro.jobpost.GetProfileResponseRecord;
import rmit.saintgiong.shared.dto.avro.jobpost.JobPostUpdateSentRecord;
import rmit.saintgiong.shared.type.KafkaTopic;


import java.time.ZoneOffset;
import java.util.ArrayList;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JobPostCreateService implements CreateJobPostInterface {

    private final JobPostMapper jobPostMapper;

    private final JobPostRepository repository;

    private final JobPostCreateValidator createValidator;

    private final KafkaTemplate<String, Object> cloudKafkaTemplate;
    private final ExternalJobPostRequestInterface externalJobPostRequestInterface;

    public JobPostCreateService(JobPostMapper jobPostMapper, JobPostRepository repository, JobPostCreateValidator createValidator,  KafkaTemplate<String, Object> cloudKafkaTemplate, ExternalJobPostRequestInterface externalJobPostRequestInterface) {
        this.jobPostMapper = jobPostMapper;
        this.repository = repository;
        this.createValidator = createValidator;
        this.cloudKafkaTemplate = cloudKafkaTemplate;
        this.externalJobPostRequestInterface = externalJobPostRequestInterface;
    }

    @Override
    @Transactional
    public CreateJobPostResponseDto createJobPost(CreateJobPostRequestDto request) {
        log.info("method=createJobPost, message=Start creating job post, param={}", request);

        // Validate business rules
        createValidator.validate(request);

        // Map from create request DTO to domain model
        JobPostEntity entity  = jobPostMapper.fromCreateCommand(request);


        // Handle skill tags
        if (request.getSkillTagIds() != null) {
            request.getSkillTagIds().forEach(entity::addSkillTag);
        }

        log.info("method=createJobPost, message=Mapped job post entity, entity={}", entity);

        // Persist the new job post
        JobPostEntity saved = repository.saveAndFlush(entity);

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

        log.info("method=createJobPost, message=Successfully created job post, id={}", saved.getId());
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

        ProducerRecord<String, Object> kafkaRequest = new ProducerRecord<>(KafkaTopic.JM_POST_ADDED_TOPIC, jobPostUpdateSentRecord);

        try {
            cloudKafkaTemplate.send(kafkaRequest);

        } catch (Exception e) {
            log.error("Error while sending");
        }


        // Build and return the response DTO
        return CreateJobPostResponseDto.builder()
                .id(String.valueOf(saved.getId()))
                .build();
    }
}
