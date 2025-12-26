package rmit.saintgiong.jobpost.domain.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpost.api.internal.CreateJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.response.CreateJobPostResponseDto;
import rmit.saintgiong.jobpost.api.internal.type.KafkaTopic;
import rmit.saintgiong.jobpost.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.validators.JobPostCreateValidator;

import rmit.saintgiong.jobpost.api.internal.dto.avro.JobPostUpdateSentRecord;
import rmit.saintgiong.jobpost.api.internal.dto.avro.JobPostUpdateResponseRecord;

import java.time.ZoneOffset;
import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JobPostCreateService implements CreateJobPostInterface {

    private final JobPostMapper jobPostMapper;

    private final JobPostRepository repository;

    private final JobPostCreateValidator createValidator;

    private final ReplyingKafkaTemplate<String, Object, Object> cloudReplyingKafkaTemplate;

    public JobPostCreateService(JobPostMapper jobPostMapper, JobPostRepository repository, JobPostCreateValidator createValidator, ReplyingKafkaTemplate<String, Object, Object> cloudReplyingKafkaTemplate) {
        this.jobPostMapper = jobPostMapper;
        this.repository = repository;
        this.createValidator = createValidator;
        this.cloudReplyingKafkaTemplate = cloudReplyingKafkaTemplate;
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

        log.info("method=createJobPost, message=Successfully created job post, id={}", saved.getId());
        JobPostUpdateSentRecord jobPostUpdateSentRecord = JobPostUpdateSentRecord.newBuilder()
                .setId(saved.getId())
                .setTitle(saved.getTitle())
                .setDescription(saved.getDescription())
                .setCity(saved.getCity())
                .setEmploymentType(new ArrayList<>(jobPostMapper.mapBitSetToStrings(saved.getEmploymentType())))
                .setSalaryTitle(saved.getSalaryTitle())
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
                .build();

        ProducerRecord<String, Object> kafkaRequest = new ProducerRecord<>(KafkaTopic.JOB_POST_ADDED_TOPIC, jobPostUpdateSentRecord);
        kafkaRequest.headers().add(KafkaHeaders.REPLY_TOPIC, KafkaTopic.JOB_POST_ADDED_REPLY_TOPIC.getBytes());

        try {
            RequestReplyFuture<String, Object, Object> responseRecord = cloudReplyingKafkaTemplate.sendAndReceive(kafkaRequest);

            ConsumerRecord<String, Object> response = responseRecord.get(10, TimeUnit.SECONDS);

            Object responseValue = response.value();
            if (responseValue instanceof JobPostUpdateResponseRecord jobpostRespose) {
                log.info(
                        "Success");
            } else {
                log.warn(
                        "Invalid response received from Kafka");
            }
        } catch (Exception e) {
            log.error("Error while sending");
        }


        // Build and return the response DTO
        return CreateJobPostResponseDto.builder()
                .id(String.valueOf(saved.getId()))
                .build();
    }
}
