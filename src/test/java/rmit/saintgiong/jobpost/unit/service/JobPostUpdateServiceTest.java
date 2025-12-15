package rmit.saintgiong.jobpost.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rmit.saintgiong.jobpost.api.internal.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpost.common.exception.DomainCode;
import rmit.saintgiong.jobpost.common.exception.DomainException;
import rmit.saintgiong.jobpost.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpost.domain.models.JobPost;
import rmit.saintgiong.jobpost.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.services.JobPostUpdateService;
import rmit.saintgiong.jobpost.domain.validators.JobPostUpdateValidator;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class JobPostUpdateServiceTest {

    @Mock
    private JobPostMapper mapper;

    @Mock
    private JobPostRepository repository;

    @Mock
    private JobPostUpdateValidator updateValidator;

    @InjectMocks
    private JobPostUpdateService updateService;

    private UpdateJobPostRequestDto requestDto;
    private JobPost jobPost;
    private UUID existingId;

    @BeforeEach
    void setUp() {
        existingId = UUID.randomUUID();

        requestDto = UpdateJobPostRequestDto.builder()
                .title("Updated Title")
                .description("Updated description")
                .city("Updated City")
                .employmentType("Part-time")
                .salaryTitle("AUD")
                .salaryMin(500.0)
                .salaryMax(1500.0)
                .expiryDate(java.time.LocalDateTime.now().plusDays(10))
                .published(Boolean.TRUE)
                .country("AU")
                .companyId(UUID.randomUUID().toString())
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
    void givenValidRequest_whenUpdateService_thenUpdateSuccessfully() {
        // Arrange
        JobPostEntity existing = JobPostEntity.builder()
                .id(existingId)
                .title("Old Title")
                .build();

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
                .id(existingId)
                .build();

        JobPostEntity saved = JobPostEntity.builder()
                .id(existingId)
                .title(toSave.getTitle())
                .build();

        when(repository.findById(existingId)).thenReturn(Optional.of(existing));
        when(mapper.fromUpdateCommand(requestDto)).thenReturn(jobPost);
        when(mapper.toEntity(jobPost)).thenReturn(toSave);
        doNothing().when(updateValidator).validate(requestDto);
        when(repository.save(toSave)).thenReturn(saved);

        // Act
        updateService.updateJobPost(existingId.toString(), requestDto);

        // Assert
        ArgumentCaptor<JobPostEntity> captor = ArgumentCaptor.forClass(JobPostEntity.class);
        verify(repository, times(1)).save(captor.capture());
        assertSame(toSave, captor.getValue());
    }

    @Test
    void givenNonExistentId_whenUpdateService_thenDomainExceptionThrown() {
        // Arrange
        when(repository.findById(existingId)).thenReturn(Optional.empty());
        // Validator shouldn't block because repository should throw not found first
        doNothing().when(updateValidator).validate(any());

        // Act & Assert
        assertThrows(DomainException.class, () -> updateService.updateJobPost(existingId.toString(), requestDto));
        // ensure save never called
        verify(repository, times(0)).save(any());
    }

    @Test
    void givenValidatorFails_whenUpdateService_thenDomainExceptionPropagates() {
        // Arrange
        JobPostEntity existing = JobPostEntity.builder().id(existingId).title("Old").build();
        doThrow(new DomainException(DomainCode.RESOURCE_NOT_FOUND, "Title conflict"))
                .when(updateValidator).validate(any(UpdateJobPostRequestDto.class));

        // Act & Assert
        assertThrows(DomainException.class, () -> updateService.updateJobPost(existingId.toString(), requestDto));
        verify(repository, times(0)).save(any());
    }
}

