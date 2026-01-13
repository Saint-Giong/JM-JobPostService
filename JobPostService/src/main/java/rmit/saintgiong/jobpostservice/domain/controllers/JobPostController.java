package rmit.saintgiong.jobpostservice.domain.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.ApplicationResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.CreateJobPostResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpostapi.internal.services.CreateJobPostInterface;
import rmit.saintgiong.jobpostapi.internal.services.DeleteJobPostInterface;
import rmit.saintgiong.jobpostapi.internal.services.QueryJobPostInterface;
import rmit.saintgiong.jobpostapi.internal.services.UpdateJobPostInterface;

import java.util.List;
import java.util.concurrent.Callable;

@RestController
@Tag(name = "Job Post", description = "Job Post Management APIs")
public class JobPostController {

    private final CreateJobPostInterface createService;

    private final UpdateJobPostInterface updateService;

    private final DeleteJobPostInterface deleteService;

    private final QueryJobPostInterface queryService;

    public JobPostController(CreateJobPostInterface createService, UpdateJobPostInterface updateService,
            DeleteJobPostInterface deleteService, QueryJobPostInterface queryService) {
        this.createService = createService;
        this.updateService = updateService;
        this.deleteService = deleteService;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a new job post", description = "Creates a new job post with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job post created successfully", content = @Content(schema = @Schema(implementation = CreateJobPostResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Callable<ResponseEntity<CreateJobPostResponseDto>> createJobPost(
            @Valid @RequestBody CreateJobPostRequestDto requestDto) {
        return () -> {
            CreateJobPostResponseDto response = createService.createJobPost(requestDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        };
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a job post by ID", description = "Retrieves details of a specific job post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job post found", content = @Content(schema = @Schema(implementation = QueryJobPostResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Job post not found")
    })
    public Callable<ResponseEntity<QueryJobPostResponseDto>> getJobPost(
            @Parameter(description = "ID of the job post to retrieve") @PathVariable @UUID String id) {
        return () -> {
            QueryJobPostResponseDto response = queryService.getJobPostById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        };
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an existing job post", description = "Updates the details of an existing job post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Job post updated successfully"),
            @ApiResponse(responseCode = "404", description = "Job post not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Callable<ResponseEntity<Void>> updateJobPost(
            @Parameter(description = "ID of the job post to update") @PathVariable @UUID String id,
            @Valid @RequestBody UpdateJobPostRequestDto requestDto) {
        return () -> {
            updateService.updateJobPost(id, requestDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        };
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job post", description = "Deletes a job post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Job post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Job post not found")
    })
    public Callable<ResponseEntity<Void>> deleteJobPost(
            @Parameter(description = "ID of the job post to delete") @PathVariable @UUID String id) {
        return () -> {
            deleteService.deleteJobPost(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        };
    }

    @GetMapping("/search/{companyId}")
    @Operation(summary = "Get job posts by Company ID", description = "Retrieves a list of job posts belonging to a specific company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of job posts retrieved successfully", content = @Content(schema = @Schema(implementation = QueryJobPostResponseDto.class)))
    })
    public Callable<ResponseEntity<List<QueryJobPostResponseDto>>> getJobPostsByCompany(
            @Parameter(description = "ID of the company to search job posts for") @PathVariable @UUID String companyId) {
        return () -> {
            List<QueryJobPostResponseDto> response = queryService.getJobPostsByCompanyId(companyId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        };
    }

    @GetMapping("/")
    @Operation(summary = "Get all job posts")
    public Callable<ResponseEntity<List<QueryJobPostResponseDto>>> getAllJobPost() {
        return () -> {
            List<QueryJobPostResponseDto> response = queryService.getAllJobPosts();
            return new ResponseEntity<>(response, HttpStatus.OK);
        };
    }

    @GetMapping("/{id}/application")
    @Operation(summary = "Get applications for a job post", description = "Retrieves a list of applications for a specific job post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of applications retrieved successfully", content = @Content(schema = @Schema(implementation = ApplicationResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Job post not found")
    })
    public Callable<ResponseEntity<List<ApplicationResponseDto>>> getApplicantsByJobPost(
            @Parameter(description = "ID of the job post to retrieve applications for") @PathVariable @UUID String id) {
        return () -> {
            List<ApplicationResponseDto> response = queryService
                    .getApplicationsByJobPostId(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        };
    }
}
