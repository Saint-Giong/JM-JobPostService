//package rmit.saintgiong.jobpost.unit.service;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
//import org.springframework.kafka.requestreply.RequestReplyFuture;
//import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
//import rmit.saintgiong.jobpostapi.internal.common.dto.response.CreateJobPostResponseDto;
//import rmit.saintgiong.jobpostapi.internal.common.type.DomainCode;
//import rmit.saintgiong.jobpostservice.common.exception.domain.DomainException;
//import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
//import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
//import rmit.saintgiong.jobpostservice.domain.services.internal.JobPostCreateService;
//import rmit.saintgiong.jobpostservice.domain.validators.JobPostCreateValidator;
//import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
//
//import java.util.BitSet;
//import java.util.Set;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//
//@ExtendWith(MockitoExtension.class)
//class JobPostCreateServiceTest {
//
//    @Mock
//    private JobPostMapper mapper;
//
//    @Mock
//    private JobPostRepository repository;
//
//    @Mock
//    private JobPostCreateValidator createValidator;
//
//    @Mock
//    private ReplyingKafkaTemplate<String, Object, Object> cloudReplyingKafkaTemplate;
//
//    @InjectMocks
//    private JobPostCreateService jobPostCreateService;
//
//    private CreateJobPostRequestDto requestDto;
//
//    @BeforeEach
//    void setUp() {
//        requestDto = CreateJobPostRequestDto.builder()
//                .title("Test Title")
//                .description("Test description")
//                .city("Test City")
//                .companyId(UUID.randomUUID().toString())
//                .employmentTypes(Set.of("FULL_TIME"))
//                .salaryTitle("AUD")
//                .salaryMin(1000.0)
//                .salaryMax(2000.0)
//                .country("AU")
//                .expiryDate(java.time.LocalDateTime.now().plusDays(30))
//                .published(Boolean.FALSE)
//                .skillTagIds(Set.of(1, 2, 3))
//                .build();
//    }
//
//    @Test
//    void givenValidRequest_whenCreateService_thenCreateSuccessfully() throws Exception {
//        // Arrange
//        UUID generatedId = UUID.randomUUID();
//
//        // This is what the mapper returns from the DTO
//        JobPostEntity mappedEntity = JobPostEntity.builder()
//                .title(requestDto.getTitle())
//                .description(requestDto.getDescription())
//                .city(requestDto.getCity())
//                .employmentType(new BitSet())
//                .salaryTitle(requestDto.getSalaryTitle())
//                .salaryMin(requestDto.getSalaryMin())
//                .salaryMax(requestDto.getSalaryMax())
//                .expiryDate(requestDto.getExpiryDate())
//                .published(requestDto.isPublished())
//                .country(requestDto.getCountry())
//                .companyId(UUID.fromString(requestDto.getCompanyId()))
//                .build();
//
//        // This is what the repository returns after save
//        JobPostEntity savedEntity = JobPostEntity.builder()
//                .id(generatedId)
//                .title(mappedEntity.getTitle())
//                .description(mappedEntity.getDescription())
//                .city(mappedEntity.getCity())
//                .employmentType(mappedEntity.getEmploymentType())
//                .salaryTitle(mappedEntity.getSalaryTitle())
//                .salaryMin(mappedEntity.getSalaryMin())
//                .salaryMax(mappedEntity.getSalaryMax())
//                .expiryDate(mappedEntity.getExpiryDate())
//                .published(mappedEntity.isPublished())
//                .country(mappedEntity.getCountry())
//                .companyId(mappedEntity.getCompanyId())
//                .postedDate(java.time.LocalDateTime.now())
//                .build();
//
//        when(mapper.fromCreateCommand(requestDto)).thenReturn(mappedEntity);
//        doNothing().when(createValidator).validate(requestDto);
//        when(repository.saveAndFlush(mappedEntity)).thenReturn(savedEntity);
//
//        // Mocking Kafka interactions - service calls mapBitSetToStrings for the Avro record
//        when(mapper.mapBitSetToStrings(any())).thenReturn(java.util.Collections.singleton("FULL_TIME"));
//
//        RequestReplyFuture<String, Object, Object> mockFuture = (RequestReplyFuture<String, Object, Object>) org.mockito.Mockito.mock(RequestReplyFuture.class);
//        ConsumerRecord<String, Object> mockRecord = new ConsumerRecord<>("topic", 0, 0, "key", "value");
//        when(mockFuture.get(anyLong(), any(TimeUnit.class))).thenReturn(mockRecord);
//        when(cloudReplyingKafkaTemplate.sendAndReceive(any(org.apache.kafka.clients.producer.ProducerRecord.class))).thenReturn(mockFuture);
//
//        // Act
//        CreateJobPostResponseDto response = jobPostCreateService.createJobPost(requestDto);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(generatedId.toString(), response.getId());
//
//        ArgumentCaptor<JobPostEntity> captor = ArgumentCaptor.forClass(JobPostEntity.class);
//        verify(repository, times(1)).saveAndFlush(captor.capture());
//        assertSame(mappedEntity, captor.getValue());
//
//        verify(cloudReplyingKafkaTemplate, times(1)).sendAndReceive(any(org.apache.kafka.clients.producer.ProducerRecord.class));
//    }
//
//    @Test
//    void givenValidatorFails_whenCreateService_thenDomainExceptionPropagates() {
//        // Arrange: validator will throw DomainException
//        doThrow(new DomainException(DomainCode.RESOURCE_NOT_FOUND, "Company not found"))
//                .when(createValidator).validate(any(CreateJobPostRequestDto.class));
//
//        // Act & Assert
//        DomainException ex = assertThrows(DomainException.class, () -> jobPostCreateService.createJobPost(requestDto));
//        assertEquals(DomainCode.RESOURCE_NOT_FOUND, ex.getDomainCode());
//
//        // repository.save should never be called
//        verify(repository, times(0)).save(any());
//    }
//}
