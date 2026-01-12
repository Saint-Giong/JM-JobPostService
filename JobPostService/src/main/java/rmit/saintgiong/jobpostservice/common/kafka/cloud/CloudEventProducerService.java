package rmit.saintgiong.jobpostservice.common.kafka.cloud;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hibernate.validator.constraints.CodePointLength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpostapi.external.services.kafka.CloudEventProducerInterface;

import java.util.concurrent.ExecutionException;

@Component
public class CloudEventProducerService implements CloudEventProducerInterface {
    private static final Logger log = LoggerFactory.getLogger(CloudEventProducerService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    public CloudEventProducerService(
            @Qualifier("cloudReplyingKafkaTemplate") ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate,
            @Qualifier("cloudKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String requestTopic, Object requestData) {
        log.debug("Sending Avro record to topic: {}", requestTopic);
        kafkaTemplate.send(requestTopic, requestData);
        log.debug("Avro record sent successfully to topic: {}", requestTopic);
    }

    @Override
    public <T> T sendAndReceive(
            String requestTopic,
            String responseTopic,
            Object requestData,
            Class<T> responseType
    ) throws ExecutionException, InterruptedException {
        log.debug("Sending Avro record to topic: {} with reply topic: {}", requestTopic, responseTopic);

        ProducerRecord<String, Object> request = new ProducerRecord<>(requestTopic, requestData);
        request.headers().add(
                KafkaHeaders.REPLY_TOPIC,
                responseTopic.getBytes()
        );

        RequestReplyFuture<String, Object, Object> futureReply = replyingKafkaTemplate.sendAndReceive(request);
        ConsumerRecord<String, Object> response = futureReply.get();

        log.debug("Received Avro response from topic: {}", responseTopic);

        return responseType.cast(response.value());
    }
}
