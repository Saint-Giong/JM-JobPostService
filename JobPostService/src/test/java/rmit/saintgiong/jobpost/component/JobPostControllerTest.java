//package rmit.saintgiong.jobpost.component;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
//import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;
//import rmit.saintgiong.jobpostapi.internal.common.dto.response.CreateJobPostResponseDto;
//import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryJobPostResponseDto;
//import rmit.saintgiong.jobpostapi.internal.common.type.DomainCode;
//import rmit.saintgiong.jobpostapi.internal.services.CreateJobPostInterface;
//import rmit.saintgiong.jobpostapi.internal.services.DeleteJobPostInterface;
//import rmit.saintgiong.jobpostapi.internal.services.QueryJobPostInterface;
//import rmit.saintgiong.jobpostapi.internal.services.UpdateJobPostInterface;
//import rmit.saintgiong.jobpostservice.common.exception.domain.DomainException;
//import rmit.saintgiong.jobpostservice.domain.controllers.JobPostController;
//
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(classes = JobPostController.class)
//@AutoConfigureMockMvc
//@DisplayName("Job Post Controller Tests")
//class JobPostControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private final ObjectMapper objectMapper = new ObjectMapper()
//            .registerModule(new JavaTimeModule())
//            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//    @MockitoBean
//    private CreateJobPostInterface createService;
//
//    @MockitoBean
//    private UpdateJobPostInterface updateService;
//
//    @MockitoBean
//    private DeleteJobPostInterface deleteService;
//
//    @MockitoBean
//    private QueryJobPostInterface queryService;
//
//    @Nested
//    @DisplayName("Create Job Post API")
//    class CreateJobPostApiTests {
//
//        private CreateJobPostRequestDto validCreateRequest;
//        private String generatedId;
//
//        @BeforeEach
//        void setUpCreate() {
//            generatedId = UUID.randomUUID().toString();
//            validCreateRequest = CreateJobPostRequestDto.builder()
//                    .title("Test Title")
//                    .description("Test description")
//                    .city("Test City")
//                    .employmentTypes(Set.of("FULL_TIME"))
//                    .salaryTitle("AUD")
//                    .salaryMin(1000.0)
//                    .salaryMax(2000.0)
//                    .expiryDate(java.time.LocalDateTime.now().plusDays(30))
//                    .published(Boolean.FALSE)
//                    .country("AU")
//                    .companyId(UUID.randomUUID().toString())
//                    .build();
//        }
//
//        @Test
//        @DisplayName("Should create job post and return id (async)")
//        void testCreateJobPost_Valid_Success() throws Exception {
//            // Arrange
//            CreateJobPostResponseDto mockResp = CreateJobPostResponseDto.builder()
//                    .id(generatedId)
//                    .build();
//
//            when(createService.createJobPost(any(CreateJobPostRequestDto.class)))
//                    .thenReturn(mockResp);
//
//            // Act
//            MvcResult result = mockMvc.perform(post("/")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(validCreateRequest)))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            // Complete async
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.id").value(generatedId));
//
//            verify(createService, times(1)).createJobPost(any(CreateJobPostRequestDto.class));
//        }
//
//        @Test
//        @DisplayName("Should fail when title is blank")
//        void testCreateJobPost_BlankTitle_Fail() throws Exception {
//            CreateJobPostRequestDto req = CreateJobPostRequestDto.builder()
//                    .title("")
//                    .description(validCreateRequest.getDescription())
//                    .city(validCreateRequest.getCity())
//                    .expiryDate(validCreateRequest.getExpiryDate())
//                    .published(validCreateRequest.isPublished())
//                    .country(validCreateRequest.getCountry())
//                    .companyId(UUID.randomUUID().toString())
//                    .build();
//
//            mockMvc.perform(post("/")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(req)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.details[*].field", Matchers.hasItem("title")));
//
//            verify(createService, never()).createJobPost(any());
//        }
//
//        @Test
//        @DisplayName("Should return 404 when service throws DomainException (resource not found)")
//        void testCreateJobPost_ServiceDomainException_Returns404() throws Exception {
//            // Arrange
//            when(createService.createJobPost(any(CreateJobPostRequestDto.class)))
//                    .thenThrow(new DomainException(
//                            DomainCode.RESOURCE_NOT_FOUND, "Company missing"));
//
//            // Act
//            MvcResult result = mockMvc.perform(post("/")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(validCreateRequest)))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.message", Matchers.containsString("RESOURCE_NOT_FOUND")));
//
//            verify(createService, times(1)).createJobPost(any(CreateJobPostRequestDto.class));
//        }
//
//        @Test
//        @DisplayName("Should return 400 when service throws IllegalArgumentException (invalid parameter)")
//        void testCreateJobPost_ServiceIllegalArgument_Returns400() throws Exception {
//            // Arrange
//            when(createService.createJobPost(any(CreateJobPostRequestDto.class)))
//                    .thenThrow(new IllegalArgumentException("Invalid UUID format"));
//
//            // Act
//            MvcResult result = mockMvc.perform(post("/")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(validCreateRequest)))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.details[0].issue", Matchers.containsString("Invalid UUID format")));
//
//            verify(createService, times(1)).createJobPost(any(CreateJobPostRequestDto.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Update Job Post API")
//    class UpdateJobPostApiTests {
//
//        private UpdateJobPostRequestDto validUpdateRequest;
//        private String existingId;
//
//        @BeforeEach
//        void setUpUpdate() {
//            existingId = UUID.randomUUID().toString();
//            validUpdateRequest = UpdateJobPostRequestDto.builder()
//                    .title("Updated Title")
//                    .description("Updated description")
//                    .city("Updated City")
//                    .employmentTypes(Set.of("PART_TIME"))
//                    .salaryTitle("AUD")
//                    .salaryMin(500.0)
//                    .salaryMax(1500.0)
//                    .expiryDate(java.time.LocalDateTime.now().plusDays(5))
//                    .published(Boolean.TRUE)
//                    .country("AU")
//                    .companyId(UUID.randomUUID().toString())
//                    .build();
//        }
//
//        @Test
//        @DisplayName("Should update job post and return 204 (async)")
//        void testUpdateJobPost_Valid_Success() throws Exception {
//            // Arrange
//            doNothing().when(updateService).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
//
//            // Act
//            MvcResult result = mockMvc.perform(patch("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(validUpdateRequest)))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            // Complete async
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isNoContent());
//
//            verify(updateService, times(1)).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
//        }
//
//        @Test
//        @DisplayName("Should return 404 when service throws DomainException")
//        void testUpdateJobPost_ServiceDomainException_Returns404() throws Exception {
//            // Arrange
//            doThrow(new DomainException(
//                    DomainCode.RESOURCE_NOT_FOUND, "Missing"))
//                    .when(updateService).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
//
//            // Act
//            MvcResult result = mockMvc.perform(patch("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(validUpdateRequest)))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isNotFound());
//
//            verify(updateService, times(1)).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
//        }
//
//        @Test
//        @DisplayName("Should return 400 when service throws IllegalArgumentException")
//        void testUpdateJobPost_ServiceIllegalArgument_Returns400() throws Exception {
//            doThrow(new IllegalArgumentException("Invalid operation"))
//                    .when(updateService).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
//
//            MvcResult result = mockMvc.perform(patch("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(validUpdateRequest)))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.details[0].issue", Matchers.containsString("Invalid operation")));
//
//            verify(updateService, times(1)).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
//        }
//
//        @Test
//        @DisplayName("Should fail when title is blank")
//        void testUpdateJobPost_BlankTitle_Fail() throws Exception {
//            UpdateJobPostRequestDto req = UpdateJobPostRequestDto.builder()
//                    .title("")
//                    .description(validUpdateRequest.getDescription())
//                    .city(validUpdateRequest.getCity())
//                    .expiryDate(validUpdateRequest.getExpiryDate())
//                    .published(validUpdateRequest.isPublished())
//                    .country(validUpdateRequest.getCountry())
//                    .companyId(UUID.randomUUID().toString())
//                    .build();
//
//            mockMvc.perform(patch("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(req)))
//                    .andExpect(status().isBadRequest());
//
//            verify(updateService, never()).updateJobPost(any(), any());
//        }
//
//        @Test
//        @DisplayName("Should fail when expiryDate is missing")
//        void testUpdateJobPost_MissingExpiryDate_Fail() throws Exception {
//            UpdateJobPostRequestDto req = UpdateJobPostRequestDto.builder()
//                    .title(validUpdateRequest.getTitle())
//                    .description(validUpdateRequest.getDescription())
//                    .city(validUpdateRequest.getCity())
//                    .published(validUpdateRequest.isPublished())
//                    .country(validUpdateRequest.getCountry())
//                    .companyId(UUID.randomUUID().toString())
//                    .build();
//
//            mockMvc.perform(patch("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(req)))
//                    .andExpect(status().isBadRequest());
//
//            verify(updateService, never()).updateJobPost(any(), any());
//        }
//
//
//        @Test
//        @DisplayName("Should return 400 when path id is invalid UUID")
//        void testUpdateJobPost_InvalidUUIDPath_Returns400() throws Exception {
//            String badId = "not-a-uuid";
//
//            mockMvc.perform(patch("/" + badId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(validUpdateRequest)))
//                    .andExpect(status().isBadRequest());
//
//            verify(updateService, never()).updateJobPost(any(), any());
//        }
//
//        @Test
//        @DisplayName("Should fail when expiry date is in the past")
//        void testUpdateJobPost_ExpiryInPast_Fail() throws Exception {
//            UpdateJobPostRequestDto req = UpdateJobPostRequestDto.builder()
//                    .title(validUpdateRequest.getTitle())
//                    .description(validUpdateRequest.getDescription())
//                    .city(validUpdateRequest.getCity())
//                    .expiryDate(java.time.LocalDateTime.now().minusDays(1))
//                    .published(validUpdateRequest.isPublished())
//                    .country(validUpdateRequest.getCountry())
//                    .companyId(UUID.randomUUID().toString())
//                    .build();
//
//            // Validator should catch this and cause 400
//            doThrow(new DomainException(DomainCode.INVALID_REQUEST_PARAMETER, "Expiry must be in future"))
//                    .when(updateService).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
//
//            MvcResult result = mockMvc.perform(patch("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(req)))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.message", Matchers.containsString("INVALID_REQUEST_PARAMETER")));
//
//            verify(updateService, times(1)).updateJobPost(any(String.class), any(UpdateJobPostRequestDto.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Get Job Posts By Company API")
//    class GetByCompanyApiTests {
//
//        private String companyId;
//
//        @BeforeEach
//        void setUp() {
//            companyId = UUID.randomUUID().toString();
//        }
//
//        @Test
//        @DisplayName("Should return list of job posts for a company (async)")
//        void testGetByCompany_Valid_Success() throws Exception {
//            QueryJobPostResponseDto dto1 = QueryJobPostResponseDto.builder()
//                    .id(UUID.randomUUID().toString())
//                    .title("Job 1")
//                    .companyId(companyId)
//                    .build();
//
//            QueryJobPostResponseDto dto2 = QueryJobPostResponseDto.builder()
//                    .id(UUID.randomUUID().toString())
//                    .title("Job 2")
//                    .companyId(companyId)
//                    .build();
//
//            List<QueryJobPostResponseDto> list = Arrays.asList(dto1, dto2);
//
//            when(queryService.getJobPostsByCompanyId(companyId)).thenReturn(list);
//
//            MvcResult result = mockMvc.perform(get("/search/" + companyId)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$[0].companyId").value(companyId))
//                    .andExpect(jsonPath("$[1].companyId").value(companyId));
//
//            verify(queryService, times(1)).getJobPostsByCompanyId(companyId);
//        }
//
//        @Test
//        @DisplayName("Should return empty list when company has no posts")
//        void testGetByCompany_EmptyList() throws Exception {
//            when(queryService.getJobPostsByCompanyId(companyId)).thenReturn(Arrays.asList());
//
//            MvcResult result = mockMvc.perform(get("/search/" + companyId)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$").isArray())
//                    .andExpect(jsonPath("$.length()").value(0));
//
//            verify(queryService, times(1)).getJobPostsByCompanyId(companyId);
//        }
//
//        @Test
//        @DisplayName("Should return 400 when company id path is invalid UUID")
//        void testGetByCompany_InvalidUUIDPath_Returns400() throws Exception {
//            String badCompanyId = "not-a-uuid";
//
//            mockMvc.perform(get("/search/" + badCompanyId)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isBadRequest());
//
//            verify(queryService, never()).getJobPostsByCompanyId(any());
//        }
//    }
//
//    @Nested
//    @DisplayName("Delete Job Post API")
//    class DeleteJobPostApiTests {
//
//        private String existingId;
//
//        @BeforeEach
//        void setUpDelete() {
//            existingId = UUID.randomUUID().toString();
//        }
//
//        @Test
//        @DisplayName("Should delete job post and return 204 (async)")
//        void testDeleteJobPost_Valid_Success() throws Exception {
//            // Arrange
//            doNothing().when(deleteService).deleteJobPost(any(String.class));
//
//            // Act
//            MvcResult result = mockMvc.perform(delete("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isNoContent());
//
//            verify(deleteService, times(1)).deleteJobPost(any(String.class));
//        }
//
//        @Test
//        @DisplayName("Should return 404 when service throws DomainException")
//        void testDeleteJobPost_ServiceDomainException_Returns404() throws Exception {
//            // Arrange
//            doThrow(new DomainException(
//                    DomainCode.RESOURCE_NOT_FOUND, "Missing"))
//                    .when(deleteService).deleteJobPost(any(String.class));
//
//            // Act
//            MvcResult result = mockMvc.perform(delete("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isNotFound());
//
//            verify(deleteService, times(1)).deleteJobPost(any(String.class));
//        }
//
//        @Test
//        @DisplayName("Should return 400 when path id is invalid UUID")
//        void testDeleteJobPost_InvalidUUIDPath_Returns400() throws Exception {
//            String badId = "not-a-uuid";
//
//            mockMvc.perform(delete("/" + badId)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isBadRequest());
//
//            verify(deleteService, never()).deleteJobPost(any());
//        }
//    }
//
//    @Nested
//    @DisplayName("Get Job Post API")
//    class GetJobPostApiTests {
//
//        private String existingId;
//
//        @BeforeEach
//        void setUpGet() {
//            existingId = UUID.randomUUID().toString();
//        }
//
//        @Test
//        @DisplayName("Should return job post details 200 (async)")
//        void testGetJobPost_Valid_Success() throws Exception {
//            // Arrange
//            QueryJobPostResponseDto mockResp =
//                    QueryJobPostResponseDto.builder()
//                            .id(existingId)
//                            .title("Title")
//                            .description("Desc")
//                            .city("City")
//                            .country("AU")
//                            .build();
//
//            when(queryService.getJobPostById(any(String.class))).thenReturn(mockResp);
//
//            // Act
//            MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/" + existingId)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            // Complete async
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.id").value(existingId))
//                    .andExpect(jsonPath("$.title").value("Title"));
//        }
//
//        @Test
//        @DisplayName("Should return 404 when service throws DomainException")
//        void testGetJobPost_ServiceDomainException_Returns404() throws Exception {
//            String id = existingId;
//
//            when(queryService.getJobPostById(any(String.class)))
//                    .thenThrow(new DomainException(DomainCode.RESOURCE_NOT_FOUND, "Missing"));
//
//            // Act
//            MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/" + id)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(request().asyncStarted())
//                    .andReturn();
//
//            mockMvc.perform(asyncDispatch(result))
//                    .andExpect(status().isNotFound());
//        }
//
//        @Test
//        @DisplayName("Should return 400 when path id is invalid UUID")
//        void testGetJobPost_InvalidUUIDPath_Returns400() throws Exception {
//            String badId = "not-a-uuid";
//
//            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/" + badId)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isBadRequest());
//        }
//    }
//}
