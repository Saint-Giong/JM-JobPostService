package rmit.saintgiong.jobpost.api.internal;

import rmit.saintgiong.jobpost.api.internal.dto.response.QueryJobPostResponseDto;

public interface QueryJobPostInterface {
    QueryJobPostResponseDto getJobPostById(String id);
}

