package rmit.saintgiong.jobpost.api.internal;

import rmit.saintgiong.jobpost.api.internal.dto.request.UpdateJobPostRequestDto;

public interface UpdateJobPostInterface {
    void updateJobPost(String id, UpdateJobPostRequestDto requestDto);
}

