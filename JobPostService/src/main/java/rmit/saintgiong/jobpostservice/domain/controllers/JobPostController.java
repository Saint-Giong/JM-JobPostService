package rmit.saintgiong.jobpostservice.domain.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
import rmit.saintgiong.jobpostapi.internal.common.dto.response.CreateJobPostResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryJobPostResponseDto;
import rmit.saintgiong.jobpostapi.internal.services.CreateJobPostInterface;
import rmit.saintgiong.jobpostapi.internal.services.DeleteJobPostInterface;
import rmit.saintgiong.jobpostapi.internal.services.QueryJobPostInterface;
import rmit.saintgiong.jobpostapi.internal.services.UpdateJobPostInterface;

import java.util.List;
import java.util.concurrent.Callable;

@RestController
@AllArgsConstructor
public class JobPostController {

    private final CreateJobPostInterface createService;

    private final UpdateJobPostInterface updateService;

    private final DeleteJobPostInterface deleteService;

    private final QueryJobPostInterface queryService;

    @PostMapping
    public Callable<ResponseEntity<CreateJobPostResponseDto>> createJobPost(@Valid @RequestBody CreateJobPostRequestDto requestDto) {
        return () -> {
            CreateJobPostResponseDto response = createService.createJobPost(requestDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        };
    }

    @GetMapping("/{id}")
    public Callable<ResponseEntity<QueryJobPostResponseDto>> getJobPost(@PathVariable @UUID String id) {
        return () -> {
            QueryJobPostResponseDto response = queryService.getJobPostById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        };
    }

    @PatchMapping("/{id}")
    public Callable<ResponseEntity<Void>> updateJobPost(@PathVariable @UUID String id, @Valid @RequestBody UpdateJobPostRequestDto requestDto) {
        return () -> {
            updateService.updateJobPost(id, requestDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        };
    }

    @DeleteMapping("/{id}")
    public Callable<ResponseEntity<Void>> deleteJobPost(@PathVariable @UUID String id) {
        return () -> {
            deleteService.deleteJobPost(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        };
    }

    @GetMapping("/search/{companyId}")
    public Callable<ResponseEntity<List<QueryJobPostResponseDto>>> getJobPostsByCompany(@PathVariable @UUID String companyId) {
        return () -> {
            List<QueryJobPostResponseDto> response = queryService.getJobPostsByCompanyId(companyId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        };
    }
}
