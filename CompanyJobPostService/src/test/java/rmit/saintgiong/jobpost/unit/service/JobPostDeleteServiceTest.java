package rmit.saintgiong.jobpost.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rmit.saintgiong.jobpostservice.common.exception.DomainCode;
import rmit.saintgiong.jobpostservice.common.exception.DomainException;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.services.JobPostDeleteService;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostBaseValidator;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rmit.saintgiong.jobpostservice.common.exception.DomainCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class JobPostDeleteServiceTest {

    @Mock
    private JobPostRepository repository;

    @Mock
    private JobPostBaseValidator<Void> baseValidator;

    @InjectMocks
    private JobPostDeleteService deleteService;

    private UUID existingId;
    private JobPostEntity existingEntity;

    @BeforeEach
    void setUp() {
        existingId = UUID.randomUUID();
        existingEntity = JobPostEntity.builder()
                .id(existingId)
                .title("To delete")
                .build();
    }

    @Test
    void givenValidId_whenDelete_thenDeletesSuccessfully() {
        // Arrange
        when(baseValidator.assertExistsById(existingId)).thenReturn(existingEntity);
        doNothing().when(repository).delete(existingEntity);

        // Act
        deleteService.deleteJobPost(existingId.toString());

        // Assert
        verify(baseValidator, times(1)).assertExistsById(existingId);
        verify(repository, times(1)).delete(existingEntity);
    }

    @Test
    void givenNonExistentId_whenDelete_thenDomainExceptionPropagates() {
        // Arrange
        when(baseValidator.assertExistsById(existingId)).thenThrow(new DomainException(RESOURCE_NOT_FOUND, "Not found"));

        // Act & Assert
        assertThrows(DomainException.class, () -> deleteService.deleteJobPost(existingId.toString()));

        verify(repository, times(0)).delete(any());
    }

    @Test
    void givenInvalidUuidString_whenDelete_thenIllegalArgumentExceptionThrown() {
        // Arrange
        String badId = "not-a-uuid";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> deleteService.deleteJobPost(badId));

        verify(baseValidator, times(0)).assertExistsById(any());
        verify(repository, times(0)).delete(any());
    }
}

