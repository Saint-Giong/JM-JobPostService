package rmit.saintgiong.jobpostapi.internal.services;

import rmit.saintgiong.jobpostapi.internal.common.dto.response.ApplicationResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryJobPostResponseDto;

import java.util.List;

public interface QueryJobPostInterface {
    QueryJobPostResponseDto getJobPostById(String id);

    List<QueryJobPostResponseDto> getJobPostsByCompanyId(String companyId);

    List<QueryJobPostResponseDto> getAllJobPosts();

    List<ApplicationResponseDto> getApplicationsByJobPostId(String postId);
}
