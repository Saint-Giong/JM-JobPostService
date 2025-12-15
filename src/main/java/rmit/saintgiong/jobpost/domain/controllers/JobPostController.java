package rmit.saintgiong.jobpost.domain.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rmit.saintgiong.jobpost.api.internal.CreateJobPostInterface;
import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.response.CreateJobPostResponseDto;

import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/v1/sgjm/jobpost", produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class JobPostController {

    private final CreateJobPostInterface createService;


    @PostMapping
    public Callable<ResponseEntity<CreateJobPostResponseDto>> createJobPost(@Valid @RequestBody CreateJobPostRequestDto requestDto) {
        return () -> {
            CreateJobPostResponseDto response = createService.createJobPost(requestDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        };
    }
}
