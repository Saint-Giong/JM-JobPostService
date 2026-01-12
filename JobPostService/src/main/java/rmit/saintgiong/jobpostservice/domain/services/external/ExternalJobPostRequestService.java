package rmit.saintgiong.jobpostservice.domain.services.external;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.jobpostapi.external.dto.avro.GetAllProfilesRequestRecord;
import rmit.saintgiong.jobpostapi.external.dto.avro.GetAllProfilesResponseRecord;
import rmit.saintgiong.jobpostapi.external.dto.avro.GetProfileRequestRecord;
import rmit.saintgiong.jobpostapi.external.dto.avro.GetProfileResponseRecord;
import rmit.saintgiong.jobpostapi.external.services.ExternalJobPostRequestInterface;
import rmit.saintgiong.jobpostapi.external.services.kafka.CloudEventProducerInterface;
import rmit.saintgiong.jobpostapi.external.services.kafka.EventProducerInterface;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryCompanyProfileResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.type.KafkaTopic;

import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
                    KafkaTopic.GET_PROFILE_REQUEST,
                    KafkaTopic.GET_PROFILE_RESPONSE,
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
            // Note: Using the exact topic names you provided (check spelling of 'REPONSE')
            GetAllProfilesResponseRecord response = eventProducer.sendAndReceive(
                    KafkaTopic.GET_ALL_PROFILE_REQUEST,
                    KafkaTopic.GET_ALL_PROFILE_RESPONSE,
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
