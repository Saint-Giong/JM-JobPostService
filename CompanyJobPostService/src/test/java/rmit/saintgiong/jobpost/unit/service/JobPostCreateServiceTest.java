package rmit.saintgiong.jobpost.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.CreateJobPostResponseDto;
import rmit.saintgiong.jobpostservice.common.exception.DomainException;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpostservice.domain.models.JobPost;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.services.JobPostCreateService;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostCreateValidator;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static rmit.saintgiong.jobpostservice.common.exception.DomainCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class JobPostCreateServiceTest {

    @Mock
    private JobPostMapper mapper;

    @Mock
    private JobPostRepository repository;

    @Mock
    private JobPostCreateValidator createValidator;

    @InjectMocks
    private JobPostCreateService jobPostCreateService;

    private CreateJobPostRequestDto requestDto;
    private JobPost jobPost;

    @BeforeEach
    void setUp() {
        requestDto = CreateJobPostRequestDto.builder()
                .title("Test Title")
                .description("Test description")
                .city("Test City")
                .companyId(UUID.randomUUID().toString())
                .employmentType("Full-time")
                .salaryTitle("AUD")
                .salaryMin(1000.0)
                .salaryMax(2000.0)
                .country("AU")
                .expiryDate(java.time.LocalDateTime.now().plusDays(30))
                .published(Boolean.FALSE)
                .build();

        jobPost = JobPost.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .city(requestDto.getCity())
                .employmentType(requestDto.getEmploymentType())
                .salaryTitle(requestDto.getSalaryTitle())
                .salaryMin(requestDto.getSalaryMin())
                .salaryMax(requestDto.getSalaryMax())
                .expiryDate(requestDto.getExpiryDate())
                .published(requestDto.isPublished())
                .country(requestDto.getCountry())
                .build();
    }

    @Test
    void givenValidRequest_whenCreateService_thenCreateSuccessfully() {
        // Arrange
        UUID generatedId = UUID.randomUUID();
        JobPostEntity toSave = JobPostEntity.builder()
                .title(jobPost.getTitle())
                .description(jobPost.getDescription())
                .city(jobPost.getCity())
                .employmentType(jobPost.getEmploymentType())
                .salaryTitle(jobPost.getSalaryTitle())
                .salaryMin(jobPost.getSalaryMin())
                .salaryMax(jobPost.getSalaryMax())
                .expiryDate(jobPost.getExpiryDate())
                .published(jobPost.isPublished())
                .country(jobPost.getCountry())
                .build();

        JobPostEntity savedEntity = JobPostEntity.builder()
                .id(generatedId)
                .title(toSave.getTitle())
                .description(toSave.getDescription())
                .city(toSave.getCity())
                .employmentType(toSave.getEmploymentType())
                .salaryTitle(toSave.getSalaryTitle())
                .salaryMin(toSave.getSalaryMin())
                .salaryMax(toSave.getSalaryMax())
                .expiryDate(toSave.getExpiryDate())
                .published(toSave.isPublished())
                .country(toSave.getCountry())
                .build();

        when(mapper.fromCreateCommand(requestDto)).thenReturn(jobPost);
        when(mapper.toEntity(jobPost)).thenReturn(toSave);
        doNothing().when(createValidator).validate(requestDto);
        when(repository.save(toSave)).thenReturn(savedEntity);

        // Act
        CreateJobPostResponseDto response = jobPostCreateService.createJobPost(requestDto);

        // Assert
        assertNotNull(response);
        assertEquals(generatedId.toString(), response.getId());

        ArgumentCaptor<JobPostEntity> captor = ArgumentCaptor.forClass(JobPostEntity.class);
        verify(repository, times(1)).save(captor.capture());
        assertSame(toSave, captor.getValue());
    }

    @Test
    void givenValidatorFails_whenCreateService_thenDomainExceptionPropagates() {
        // Arrange: validator will throw DomainException
        doThrow(new DomainException(RESOURCE_NOT_FOUND, "Company not found"))
                .when(createValidator).validate(any(CreateJobPostRequestDto.class));

        // Act & Assert
        DomainException ex = assertThrows(DomainException.class, () -> jobPostCreateService.createJobPost(requestDto));
        assertEquals(RESOURCE_NOT_FOUND, ex.getDomainCode());

        // repository.save should never be called
        verify(repository, times(0)).save(any());
    }
}
