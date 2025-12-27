package rmit.saintgiong.jobpostapi.internal.services;

import rmit.saintgiong.jobpostapi.internal.common.dto.request.UpdateJobPostRequestDto;

public interface UpdateJobPostInterface {
    void updateJobPost(String id, UpdateJobPostRequestDto requestDto);
}

