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
//import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;
//import rmit.saintgiong.jobpostapi.internal.common.type.DomainCode;
//import rmit.saintgiong.jobpostservice.common.exception.domain.DomainException;
//import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
//import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
//import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
//import rmit.saintgiong.jobpostservice.domain.services.internal.JobPostUpdateService;
//import rmit.saintgiong.jobpostservice.domain.validators.JobPostUpdateValidator;
//
//import java.util.BitSet;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
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
//class JobPostUpdateServiceTest {
//
//    @Mock
//    private JobPostMapper mapper;
//
//    @Mock
//    private JobPostRepository repository;
//
//    @Mock
//    private JobPostUpdateValidator updateValidator;
//
//    @Mock
//    private ReplyingKafkaTemplate<String, Object, Object> cloudReplyingKafkaTemplate;
//
//    @InjectMocks
//    private JobPostUpdateService updateService;
//
//    private UpdateJobPostRequestDto requestDto;
//    private UUID existingId;
//
//    @BeforeEach
//    void setUp() {
//        existingId = UUID.randomUUID();
//
//        requestDto = UpdateJobPostRequestDto.builder()
//                .title("Updated Title")
//                .description("Updated description")
//                .city("Updated City")
//                .employmentTypes(java.util.Set.of("PART_TIME"))
//                .salaryTitle("AUD")
//                .salaryMin(500.0)
//                .salaryMax(1500.0)
//                .expiryDate(java.time.LocalDateTime.now().plusDays(10))
//                .published(Boolean.TRUE)
//                .country("AU")
//                .companyId(UUID.randomUUID().toString())
//                .build();
//    }
//
//    @Test
//    void givenValidRequest_whenUpdateService_thenUpdateSuccessfully() throws Exception {
//        // Arrange
//        JobPostEntity existing = JobPostEntity.builder()
//                .id(existingId)
//                .title("Old Title")
//                .build();
//
//        JobPostEntity updatedEntity = JobPostEntity.builder()
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
//        JobPostEntity saved = JobPostEntity.builder()
//                .id(existingId)
//                .title(updatedEntity.getTitle())
//                .description(updatedEntity.getDescription())
//                .city(updatedEntity.getCity())
//                .salaryTitle(updatedEntity.getSalaryTitle())
//                .salaryMin(updatedEntity.getSalaryMin())
//                .salaryMax(updatedEntity.getSalaryMax())
//                .country(updatedEntity.getCountry())
//                .postedDate(java.time.LocalDateTime.now())
//                .expiryDate(updatedEntity.getExpiryDate())
//                .build();
//
//        when(repository.findById(existingId)).thenReturn(Optional.of(existing));
//        when(mapper.fromUpdateCommand(requestDto)).thenReturn(updatedEntity);
//        // The service logic sets ID and PostedDate from existing entity to updatedEntity
//        // And then saves updatedEntity
//
//        when(mapper.mapBitSetToStrings(any())).thenReturn(java.util.Collections.singleton("PART_TIME"));
//        doNothing().when(updateValidator).validate(requestDto);
//        when(repository.saveAndFlush(updatedEntity)).thenReturn(saved);
//
//        // Mocking Kafka interactions
//        RequestReplyFuture<String, Object, Object> mockFuture = (RequestReplyFuture<String, Object, Object>) org.mockito.Mockito.mock(RequestReplyFuture.class);
//        ConsumerRecord<String, Object> mockRecord = new ConsumerRecord<>("topic", 0, 0, "key", "value");
//        when(mockFuture.get(anyLong(), any(TimeUnit.class))).thenReturn(mockRecord);
//        when(cloudReplyingKafkaTemplate.sendAndReceive(any(org.apache.kafka.clients.producer.ProducerRecord.class))).thenReturn(mockFuture);
//
//
//        // Act
//        updateService.updateJobPost(existingId.toString(), requestDto);
//
//        // Assert
//        ArgumentCaptor<JobPostEntity> captor = ArgumentCaptor.forClass(JobPostEntity.class);
//        verify(repository, times(1)).saveAndFlush(captor.capture());
//        assertSame(updatedEntity, captor.getValue());
//
//        verify(cloudReplyingKafkaTemplate, times(1)).sendAndReceive(any(org.apache.kafka.clients.producer.ProducerRecord.class));
//    }
//
//    @Test
//    void givenNonExistentId_whenUpdateService_thenDomainExceptionThrown() {
//        // Arrange
//        when(repository.findById(existingId)).thenReturn(Optional.empty());
//        // Validator shouldn't block because repository should throw not found first
//        doNothing().when(updateValidator).validate(any());
//
//        // Act & Assert
//        assertThrows(DomainException.class, () -> updateService.updateJobPost(existingId.toString(), requestDto));
//        // ensure save never called
//        verify(repository, times(0)).save(any());
//    }
//
//    @Test
//    void givenValidatorFails_whenUpdateService_thenDomainExceptionPropagates() {
//        // Arrange
//        doThrow(new DomainException(DomainCode.RESOURCE_NOT_FOUND, "Title conflict"))
//                .when(updateValidator).validate(any(UpdateJobPostRequestDto.class));
//
//        // Act & Assert
//        assertThrows(DomainException.class, () -> updateService.updateJobPost(existingId.toString(), requestDto));
//        verify(repository, times(0)).save(any());
//    }
//}
