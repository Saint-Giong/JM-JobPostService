package rmit.saintgiong.jobpostapi.external.services.kafka;

public interface CloudEventProducerInterface {
    void send(String requestTopic, Object requestData);

//    <T> T sendAndReceive(
//            String requestTopic,
//            String responseTopic,
//            Object requestData,
//            Class<T> responseType
//    ) throws ExecutionException, InterruptedException;
}
