//package rmit.saintgiong.jobpost.common.kafka.local;
//
//import io.confluent.kafka.serializers.KafkaAvroSerializer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//import java.util.Map;
//
//@Configuration
//public class LocalKafkaProducerConfig {
//
//    private final LocalKafkaConfig localKafkaConfig;
//
//    public LocalKafkaProducerConfig(LocalKafkaConfig localKafkaConfig) {
//        this.localKafkaConfig = localKafkaConfig;
//    }
//
//    @Bean(name = "localProducerFactory")
//    public ProducerFactory<String, Object> localProducerFactory() {
//        Map<String, Object> config = localKafkaConfig.getLocalConfigs();
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
//        return new DefaultKafkaProducerFactory<>(config);
//    }
//
//    @Bean(name = "localKafkaTemplate")
//    public KafkaTemplate<String, Object> localKafkaTemplate() {
//        return new KafkaTemplate<>(localProducerFactory());
//    }
//}
