package rmit.saintgiong.jobpostservice.domain.services.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpostapi.external.services.ExternalJobPostRequestInterface;
import rmit.saintgiong.jobpostapi.external.services.kafka.CloudEventProducerInterface;
import rmit.saintgiong.jobpostapi.external.services.kafka.EventProducerInterface;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryCompanyProfileResponseDto;

import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.shared.dto.avro.jobpost.GetAllProfilesRequestRecord;
import rmit.saintgiong.shared.dto.avro.jobpost.GetAllProfilesResponseRecord;
import rmit.saintgiong.shared.dto.avro.jobpost.GetProfileRequestRecord;
import rmit.saintgiong.shared.dto.avro.jobpost.GetProfileResponseRecord;
import rmit.saintgiong.shared.type.KafkaTopic;


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExternalJobPostRequestService implements ExternalJobPostRequestInterface {

    private final CloudEventProducerInterface cloudEventProducerInterface;
    private final EventProducerInterface eventProducer;
    private final JobPostMapper jobPostMapper;

    public ExternalJobPostRequestService(CloudEventProducerInterface cloudEventProducerInterface, EventProducerInterface eventProducer, JobPostMapper jobPostMapper) {
        this.cloudEventProducerInterface = cloudEventProducerInterface;
        this.eventProducer = eventProducer;
        this.jobPostMapper = jobPostMapper;
    }

    @Override
    public QueryCompanyProfileResponseDto sendGetProfileRequest(UUID companyId) {

        try {
            GetProfileRequestRecord getProfileRequestRecord = GetProfileRequestRecord.newBuilder()
                    .setId(companyId)
                    .build();

            GetProfileResponseRecord response = eventProducer.sendAndReceive(
                    KafkaTopic.JM_GET_PROFILE_REQUEST_TOPIC,
                    KafkaTopic.JM_GET_PROFILE_RESPONSE_TOPIC,
                    getProfileRequestRecord,
                    GetProfileResponseRecord.class
            );

            QueryCompanyProfileResponseDto result = jobPostMapper.fromAvroRecord(response);


            return result;

        } catch (ExecutionException | InterruptedException e) {
            return QueryCompanyProfileResponseDto.builder()
                    .id(null)
                    .build();
        }
    }

    @Override
    public List<QueryCompanyProfileResponseDto> sendGetAllProfilesRequest() {
        try {
            GetAllProfilesRequestRecord request = GetAllProfilesRequestRecord.newBuilder()
                    .setRequestId(UUID.randomUUID())
                    .build();

            // 2. Send and wait for reply
            GetAllProfilesResponseRecord response = eventProducer.sendAndReceive(
                    KafkaTopic.JM_GET_ALL_PROFILES_REQUEST_TOPIC,
                    KafkaTopic.JM_GET_ALL_PROFILES_RESPONSE_TOPIC,
                    request,
                    GetAllProfilesResponseRecord.class
            );

            if (response == null || response.getProfiles() == null) {
                return Collections.emptyList();
            }

            // 3. Map the Avro List to DTO List
            return response.getProfiles().stream()
                    .map(avroProfile -> QueryCompanyProfileResponseDto.builder()
                            .id(avroProfile.getId() != null ? avroProfile.getId().toString() : null)
                            .name(avroProfile.getName() != null ? avroProfile.getName() : null)
                            .phone(avroProfile.getPhone() != null ? avroProfile.getPhone() : null)
                            .address(avroProfile.getAddress() != null ? avroProfile.getAddress() : null)
                            .city(avroProfile.getCity() != null ? avroProfile.getCity() : null)
                            .aboutUs(avroProfile.getAboutUs() != null ? avroProfile.getAboutUs() : null)
                            .admissionDescription(avroProfile.getAdmissionDescription() != null ? avroProfile.getAdmissionDescription().toString() : null)
                            .logoUrl(avroProfile.getLogoUrl() != null ? avroProfile.getLogoUrl() : null)
                            .country(avroProfile.getCountry() != null ? avroProfile.getCountry() : null)
                            .build()
                    )
                    .collect(Collectors.toList());

        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to fetch all profiles via Kafka", e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Unexpected error in sendGetAllProfilesRequest", e);
            return Collections.emptyList();
        }
    }


}
