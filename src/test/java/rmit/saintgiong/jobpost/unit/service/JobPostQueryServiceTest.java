package rmit.saintgiong.jobpost.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rmit.saintgiong.jobpost.api.internal.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpost.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpost.domain.services.JobPostQueryService;
import rmit.saintgiong.jobpost.domain.validators.JobPostBaseValidator;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobPostQueryServiceTest {

    @Mock
    private JobPostMapper mapper;

    @Mock
    private JobPostBaseValidator<Void> baseValidator;

    @InjectMocks
    private JobPostQueryService queryService;

    private UUID existingId;
    private JobPostEntity existingEntity;

    @BeforeEach
    void setUp() {
        existingId = UUID.randomUUID();
        existingEntity = JobPostEntity.builder()
                .id(existingId)
                .title("Title")
                .description("Desc")
                .city("City")
                .country("AU")
                .build();
    }

    @Test
    void givenExistingId_whenGetJobPost_thenReturnsResponse() {
        when(baseValidator.assertExistsById(existingId)).thenReturn(existingEntity);

        QueryJobPostResponseDto dto = QueryJobPostResponseDto.builder()
                .id(existingId.toString())
                .title(existingEntity.getTitle())
                .description(existingEntity.getDescription())
                .city(existingEntity.getCity())
                .country(existingEntity.getCountry())
                .build();

        when(mapper.toQueryResponse(existingEntity)).thenReturn(dto);

        QueryJobPostResponseDto result = queryService.getJobPostById(existingId.toString());

        assertEquals(existingId.toString(), result.getId());
        assertEquals("Title", result.getTitle());
    }

    @Test
    void givenNonExistentId_whenGetJobPost_thenDomainExceptionPropagates() {
        when(baseValidator.assertExistsById(existingId)).thenThrow(new rmit.saintgiong.jobpost.common.exception.DomainException(
                rmit.saintgiong.jobpost.common.exception.DomainCode.RESOURCE_NOT_FOUND, "Not found"));

        assertThrows(rmit.saintgiong.jobpost.common.exception.DomainException.class, () ->
                queryService.getJobPostById(existingId.toString()));
    }

    @Test
    void givenInvalidUuidString_whenGetJobPost_thenIllegalArgumentExceptionThrown() {
        String badId = "not-a-uuid";
        assertThrows(IllegalArgumentException.class, () -> queryService.getJobPostById(badId));
    }
}

