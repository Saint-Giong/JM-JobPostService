package rmit.saintgiong.jobpostapi.internal.services;

import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.CreateJobPostResponseDto;

public interface CreateJobPostInterface {
    CreateJobPostResponseDto createJobPost(CreateJobPostRequestDto request);
}

