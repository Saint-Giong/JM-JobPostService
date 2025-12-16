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
import rmit.saintgiong.jobpost.api.internal.QueryJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.UpdateJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.DeleteJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.response.CreateJobPostResponseDto;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import rmit.saintgiong.jobpost.common.exception.DomainCode;
import rmit.saintgiong.jobpost.common.exception.DomainException;
import rmit.saintgiong.jobpost.api.internal.dto.response.QueryJobPostResponseDto;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    @MockitoBean
    private UpdateJobPostInterface updateService;

    @MockitoBean
    private DeleteJobPostInterface deleteService;

    @MockitoBean
    private QueryJobPostInterface queryService;

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
                    .thenThrow(new DomainException(
                            DomainCode.RESOURCE_NOT_FOUND, "Company missing"));

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

    @Nested
    @DisplayName("Update Job Post API")
    class UpdateJobPostApiTests {

        private UpdateJobPostRequestDto validUpdateRequest;
        private String existingId;

        @BeforeEach
        void setUpUpdate() {
            existingId = UUID.randomUUID().toString();
            validUpdateRequest = UpdateJobPostRequestDto.builder()
                    .title("Updated Title")
                    .description("Updated description")
                    .city("Updated City")
                    .employmentType("Part-time")
                    .salaryTitle("AUD")
                    .salaryMin(500.0)
                    .salaryMax(1500.0)
                    .expiryDate(java.time.LocalDateTime.now().plusDays(5))
                    .published(Boolean.TRUE)
                    .country("AU")
                    .companyId(UUID.randomUUID().toString())
                    .build();
        }

        @Test
        @DisplayName("Should update job post and return 204 (async)")
        void testUpdateJobPost_Valid_Success() throws Exception {
            // Arrange
            doNothing().when(updateService).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));

            // Act
            MvcResult result = mockMvc.perform(patch("/v1/sgjm/jobpost/" + existingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateRequest)))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // Complete async
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isNoContent());

            verify(updateService, times(1)).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
        }

        @Test
        @DisplayName("Should return 404 when service throws DomainException")
        void testUpdateJobPost_ServiceDomainException_Returns404() throws Exception {
            // Arrange
            doThrow(new DomainException(
                    DomainCode.RESOURCE_NOT_FOUND, "Missing"))
                    .when(updateService).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));

            // Act
            MvcResult result = mockMvc.perform(patch("/v1/sgjm/jobpost/" + existingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateRequest)))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isNotFound());

            verify(updateService, times(1)).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
        }

        @Test
        @DisplayName("Should fail when title is blank")
        void testUpdateJobPost_BlankTitle_Fail() throws Exception {
            UpdateJobPostRequestDto req = UpdateJobPostRequestDto.builder()
                    .title("")
                    .description(validUpdateRequest.getDescription())
                    .city(validUpdateRequest.getCity())
                    .expiryDate(validUpdateRequest.getExpiryDate())
                    .published(validUpdateRequest.isPublished())
                    .country(validUpdateRequest.getCountry())
                    .companyId(UUID.randomUUID().toString())
                    .build();

            mockMvc.perform(patch("/v1/sgjm/jobpost/" + existingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());

            verify(updateService, never()).updateJobPost(any(), any());
        }

        @Test
        @DisplayName("Should fail when expiryDate is missing")
        void testUpdateJobPost_MissingExpiryDate_Fail() throws Exception {
            UpdateJobPostRequestDto req = UpdateJobPostRequestDto.builder()
                    .title(validUpdateRequest.getTitle())
                    .description(validUpdateRequest.getDescription())
                    .city(validUpdateRequest.getCity())
                    .published(validUpdateRequest.isPublished())
                    .country(validUpdateRequest.getCountry())
                    .companyId(UUID.randomUUID().toString())
                    .build();

            mockMvc.perform(patch("/v1/sgjm/jobpost/" + existingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());

            verify(updateService, never()).updateJobPost(any(), any());
        }


        @Test
        @DisplayName("Should return 400 when path id is invalid UUID")
        void testUpdateJobPost_InvalidUUIDPath_Returns400() throws Exception {
            String badId = "not-a-uuid";

            mockMvc.perform(patch("/v1/sgjm/jobpost/" + badId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateRequest)))
                    .andExpect(status().isBadRequest());

            verify(updateService, never()).updateJobPost(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Job Post API")
    class DeleteJobPostApiTests {

        private String existingId;

        @BeforeEach
        void setUpDelete() {
            existingId = UUID.randomUUID().toString();
        }

        @Test
        @DisplayName("Should delete job post and return 204 (async)")
        void testDeleteJobPost_Valid_Success() throws Exception {
            // Arrange
            doNothing().when(deleteService).deleteJobPost(any(String.class));

            // Act
            MvcResult result = mockMvc.perform(delete("/v1/sgjm/jobpost/" + existingId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isNoContent());

            verify(deleteService, times(1)).deleteJobPost(any(String.class));
        }

        @Test
        @DisplayName("Should return 404 when service throws DomainException")
        void testDeleteJobPost_ServiceDomainException_Returns404() throws Exception {
            // Arrange
            doThrow(new DomainException(
                    DomainCode.RESOURCE_NOT_FOUND, "Missing"))
                    .when(deleteService).deleteJobPost(any(String.class));

            // Act
            MvcResult result = mockMvc.perform(delete("/v1/sgjm/jobpost/" + existingId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isNotFound());

            verify(deleteService, times(1)).deleteJobPost(any(String.class));
        }

        @Test
        @DisplayName("Should return 400 when path id is invalid UUID")
        void testDeleteJobPost_InvalidUUIDPath_Returns400() throws Exception {
            String badId = "not-a-uuid";

            mockMvc.perform(delete("/v1/sgjm/jobpost/" + badId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(deleteService, never()).deleteJobPost(any());
        }
    }

    @Nested
    @DisplayName("Get Job Post API")
    class GetJobPostApiTests {

        private String existingId;

        @BeforeEach
        void setUpGet() {
            existingId = UUID.randomUUID().toString();
        }

        @Test
        @DisplayName("Should return job post details 200 (async)")
        void testGetJobPost_Valid_Success() throws Exception {
            // Arrange
            QueryJobPostResponseDto mockResp =
                    QueryJobPostResponseDto.builder()
                            .id(existingId)
                            .title("Title")
                            .description("Desc")
                            .city("City")
                            .country("AU")
                            .build();

            when(queryService.getJobPostById(any(String.class))).thenReturn(mockResp);

            // Act
            MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/sgjm/jobpost/" + existingId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // Complete async
            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(existingId))
                    .andExpect(jsonPath("$.title").value("Title"));
        }

        @Test
        @DisplayName("Should return 404 when service throws DomainException")
        void testGetJobPost_ServiceDomainException_Returns404() throws Exception {
            String id = existingId;

            when(queryService.getJobPostById(any(String.class)))
                    .thenThrow(new DomainException(DomainCode.RESOURCE_NOT_FOUND, "Missing"));

            // Act
            MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/sgjm/jobpost/" + id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when path id is invalid UUID")
        void testGetJobPost_InvalidUUIDPath_Returns400() throws Exception {
            String badId = "not-a-uuid";

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/sgjm/jobpost/" + badId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}
