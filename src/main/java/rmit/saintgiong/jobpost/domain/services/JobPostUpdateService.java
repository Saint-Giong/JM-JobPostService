package rmit.saintgiong.jobpost.domain.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpost.api.internal.UpdateJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.type.KafkaTopic;
import rmit.saintgiong.jobpost.common.exception.DomainException;
import rmit.saintgiong.jobpost.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpost.domain.models.JobPost;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.validators.JobPostUpdateValidator;

import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


import rmit.saintgiong.jobpost.api.internal.dto.avro.JobPostUpdateSentRecord;
import rmit.saintgiong.jobpost.api.internal.dto.avro.JobPostUpdateResponseRecord;

import static rmit.saintgiong.jobpost.common.exception.DomainCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
@Slf4j
public class JobPostUpdateService implements UpdateJobPostInterface {

    private final JobPostMapper mapper;
    private final JobPostRepository repository;
    private final JobPostUpdateValidator updateValidator;

    private final ReplyingKafkaTemplate<String, Object, Object> cloudReplyingKafkaTemplate;

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

        JobPost updatedModel = mapper.fromUpdateCommand(requestDto);
        updatedModel.setId(existing.getId());
        updatedModel.setPostedDate(existing.getPostedDate());

        JobPostEntity updatedEntity = mapper.toEntity(updatedModel);
        JobPostEntity saved = repository.saveAndFlush(updatedEntity);

        JobPostUpdateSentRecord jobPostUpdateSentRecord = JobPostUpdateSentRecord.newBuilder()
                .setAction("updated")
                .setId(saved.getId())
                .setTitle(saved.getTitle())
                .setDescription(saved.getDescription())
                .setCity(saved.getCity())
                .setEmploymentType(saved.getEmploymentType())
                .setSalaryTitle(saved.getSalaryTitle())
                .setSalaryMin(saved.getSalaryMin())
                .setSalaryMax(saved.getSalaryMax())
                .setPostedDate(saved.getPostedDate().toInstant(ZoneOffset.UTC))
                .setExpiryDate(saved.getExpiryDate().toInstant(ZoneOffset.UTC))
                .setPublished(saved.isPublished())
                .setCountry(saved.getCountry())
                .setCompanyId(saved.getCompanyId())
                .build();

        ProducerRecord<String, Object> kafkaRequest = new ProducerRecord<>(KafkaTopic.JOB_POST_UPDATED_TOPIC, jobPostUpdateSentRecord);
        kafkaRequest.headers().add(KafkaHeaders.REPLY_TOPIC, KafkaTopic.JOB_POST_UPDATED_REPLY_TOPIC.getBytes());

        try {
            RequestReplyFuture<String, Object, Object> responseRecord = cloudReplyingKafkaTemplate.sendAndReceive(kafkaRequest);

            ConsumerRecord<String, Object> response = responseRecord.get(10, TimeUnit.SECONDS);

            Object responseValue = response.value();
            if (responseValue instanceof rmit.saintgiong.jobpost.api.internal.dto.avro.JobPostUpdateResponseRecord jobpostRespose) {
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
