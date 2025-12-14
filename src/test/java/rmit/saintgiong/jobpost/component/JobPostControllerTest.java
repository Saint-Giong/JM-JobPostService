package rmit.saintgiong.jobpost.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rmit.saintgiong.jobpost.JobpostApplication;
import rmit.saintgiong.jobpost.api.internal.CreateJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.response.CreateJobPostResponseDto;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = JobpostApplication.class)
@AutoConfigureMockMvc
@DisplayName("Job Post Controller Tests")
class JobPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateJobPostInterface createService;

    @Nested
    @DisplayName("Create Job Post API")
    class CreateJobPostApiTests {

        private CreateJobPostRequestDto validCreateRequest;
        private String generatedId;

        @BeforeEach
        void setUpCreate() {
            generatedId = UUID.randomUUID().toString();
            validCreateRequest = CreateJobPostRequestDto.builder()
                    .title("Test Title")
                    .description("Test description")
                    .city("Test City")
                    .employmentType("Full-time")
                    .salaryTitle("AUD")
                    .salaryMin(1000.0)
                    .salaryMax(2000.0)
                    .expiryDate(java.time.LocalDateTime.now().plusDays(30))
                    .published(Boolean.FALSE)
                    .country("AU")
                    .companyId(UUID.randomUUID().toString())
                    .build();
        }

        @Test
        @DisplayName("Should create job post and return id (async)")
        void testCreateJobPost_Valid_Success() throws Exception {
            // Arrange
            CreateJobPostResponseDto mockResp = CreateJobPostResponseDto.builder()
                    .id(generatedId)
                    .build();

            when(createService.createJobPost(any(CreateJobPostRequestDto.class)))
                    .thenReturn(mockResp);

            // Act
            MvcResult result = mockMvc.perform(post("/v1/sgjm/jobpost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // Complete async
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(generatedId));

            verify(createService, times(1)).createJobPost(any(CreateJobPostRequestDto.class));
        }

        @Test
        @DisplayName("Should fail when title is blank")
        void testCreateJobPost_BlankTitle_Fail() throws Exception {
            CreateJobPostRequestDto req = CreateJobPostRequestDto.builder()
                    .title("")
                    .description(validCreateRequest.getDescription())
                    .city(validCreateRequest.getCity())
                    .expiryDate(validCreateRequest.getExpiryDate())
                    .published(validCreateRequest.isPublished())
                    .country(validCreateRequest.getCountry())
                    .companyId(UUID.randomUUID().toString())
                    .build();

            mockMvc.perform(post("/v1/sgjm/jobpost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[*].field", Matchers.hasItem("title")));

            verify(createService, never()).createJobPost(any());
        }

        @Test
        @DisplayName("Should return 404 when service throws DomainException (resource not found)")
        void testCreateJobPost_ServiceDomainException_Returns404() throws Exception {
            // Arrange
            when(createService.createJobPost(any(CreateJobPostRequestDto.class)))
                    .thenThrow(new rmit.saintgiong.jobpost.common.exception.DomainException(
                            rmit.saintgiong.jobpost.common.exception.DomainCode.RESOURCE_NOT_FOUND, "Company missing"));

            // Act
            MvcResult result = mockMvc.perform(post("/v1/sgjm/jobpost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", Matchers.containsString("RESOURCE_NOT_FOUND")));

            verify(createService, times(1)).createJobPost(any(CreateJobPostRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 when service throws IllegalArgumentException (invalid parameter)")
        void testCreateJobPost_ServiceIllegalArgument_Returns400() throws Exception {
            // Arrange
            when(createService.createJobPost(any(CreateJobPostRequestDto.class)))
                    .thenThrow(new IllegalArgumentException("Invalid UUID format"));

            // Act
            MvcResult result = mockMvc.perform(post("/v1/sgjm/jobpost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0].issue", Matchers.containsString("Invalid UUID format")));

            verify(createService, times(1)).createJobPost(any(CreateJobPostRequestDto.class));
        }
    }
}
