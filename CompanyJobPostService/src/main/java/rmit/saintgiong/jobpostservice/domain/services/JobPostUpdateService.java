package rmit.saintgiong.jobpostservice.domain.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpost.api.internal.dto.avro.JobPostUpdateResponseRecord;
import rmit.saintgiong.jobpost.api.internal.dto.avro.JobPostUpdateSentRecord;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.type.KafkaTopic;
import rmit.saintgiong.jobpostapi.internal.services.UpdateJobPostInterface;
import rmit.saintgiong.jobpostservice.common.exception.DomainException;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostUpdateValidator;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpostapi.internal.common.type.DomainCode;
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

    private final ReplyingKafkaTemplate<String, Object, Object> cloudReplyingKafkaTemplate;

    public JobPostUpdateService(JobPostMapper jobPostMapper, JobPostRepository repository, JobPostUpdateValidator updateValidator, ReplyingKafkaTemplate<String, Object, Object> cloudReplyingKafkaTemplate) {
        this.jobPostMapper = jobPostMapper;
        this.repository = repository;
        this.updateValidator = updateValidator;
        this.cloudReplyingKafkaTemplate = cloudReplyingKafkaTemplate;
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

        ProducerRecord<String, Object> kafkaRequest = new ProducerRecord<>(KafkaTopic.JOB_POST_UPDATED_TOPIC, jobPostUpdateSentRecord);
        kafkaRequest.headers().add(KafkaHeaders.REPLY_TOPIC, KafkaTopic.JOB_POST_UPDATED_REPLY_TOPIC.getBytes());

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

        log.info("method=updateJobPost, message=Successfully updated job post, id={}", saved.getId());
    }
}
