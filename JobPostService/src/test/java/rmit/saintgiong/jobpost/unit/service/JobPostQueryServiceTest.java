package rmit.saintgiong.jobpost.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpostservice.common.exception.DomainException;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;
import rmit.saintgiong.jobpostservice.domain.services.JobPostQueryService;
import rmit.saintgiong.jobpostservice.domain.validators.JobPostBaseValidator;

import java.util.UUID;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static rmit.saintgiong.jobpostservice.common.exception.DomainCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class JobPostQueryServiceTest {

    @Mock
    private JobPostMapper mapper;

    @Mock
    private JobPostBaseValidator<Void> baseValidator;

    @Mock
    private JobPostRepository repository;

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
        when(baseValidator.assertExistsById(existingId)).thenThrow(new DomainException(
                RESOURCE_NOT_FOUND, "Not found"));

        assertThrows(DomainException.class, () ->
                queryService.getJobPostById(existingId.toString()));
    }

    @Test
    void givenInvalidUuidString_whenGetJobPost_thenIllegalArgumentExceptionThrown() {
        String badId = "not-a-uuid";
        assertThrows(IllegalArgumentException.class, () -> queryService.getJobPostById(badId));
    }

    // New tests for getJobPostsByCompanyId

    @Test
    void givenCompanyWithPosts_whenGetByCompany_thenReturnsMappedList() {
        UUID companyId = UUID.randomUUID();

        JobPostEntity e1 = JobPostEntity.builder()
                .id(UUID.randomUUID())
                .title("Job A")
                .companyId(companyId)
                .build();

        JobPostEntity e2 = JobPostEntity.builder()
                .id(UUID.randomUUID())
                .title("Job B")
                .companyId(companyId)
                .build();

        List<JobPostEntity> entities = Arrays.asList(e1, e2);

        // Validator should be called to assert company exists
        doNothing().when(baseValidator).assertCompanyExists(companyId);

        when(repository.findAllByCompanyId(companyId)).thenReturn(entities);

        QueryJobPostResponseDto dto1 = QueryJobPostResponseDto.builder()
                .id(e1.getId().toString())
                .title(e1.getTitle())
                .companyId(companyId.toString())
                .build();

        QueryJobPostResponseDto dto2 = QueryJobPostResponseDto.builder()
                .id(e2.getId().toString())
                .title(e2.getTitle())
                .companyId(companyId.toString())
                .build();

        when(mapper.toQueryResponse(e1)).thenReturn(dto1);
        when(mapper.toQueryResponse(e2)).thenReturn(dto2);

        List<QueryJobPostResponseDto> result = queryService.getJobPostsByCompanyId(companyId.toString());

        assertEquals(2, result.size());
        assertEquals(companyId.toString(), result.get(0).getCompanyId());
        assertEquals(companyId.toString(), result.get(1).getCompanyId());
    }

    @Test
    void givenCompanyWithNoPosts_whenGetByCompany_thenReturnsEmptyList() {
        UUID companyId = UUID.randomUUID();

        doNothing().when(baseValidator).assertCompanyExists(companyId);
        when(repository.findAllByCompanyId(companyId)).thenReturn(Arrays.asList());

        List<QueryJobPostResponseDto> result = queryService.getJobPostsByCompanyId(companyId.toString());

        assertEquals(0, result.size());
    }

    @Test
    void givenInvalidUuid_whenGetByCompany_thenIllegalArgumentExceptionThrown() {
        String badCompanyId = "not-a-uuid";
        assertThrows(IllegalArgumentException.class, () -> queryService.getJobPostsByCompanyId(badCompanyId));
    }

    @Test
    void givenValidatorThrows_whenGetByCompany_thenDomainExceptionPropagates() {
        UUID companyId = UUID.randomUUID();

        doThrow(new DomainException(RESOURCE_NOT_FOUND, "Company missing"))
                .when(baseValidator).assertCompanyExists(companyId);

        assertThrows(DomainException.class, () ->
                queryService.getJobPostsByCompanyId(companyId.toString()));
    }
}
