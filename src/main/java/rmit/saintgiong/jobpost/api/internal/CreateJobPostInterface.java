package rmit.saintgiong.jobpost.api.internal;

import rmit.saintgiong.jobpost.api.internal.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpost.api.internal.dto.response.CreateJobPostResponseDto;

public interface CreateJobPostInterface {
    CreateJobPostResponseDto createJobPost(CreateJobPostRequestDto request);
}

