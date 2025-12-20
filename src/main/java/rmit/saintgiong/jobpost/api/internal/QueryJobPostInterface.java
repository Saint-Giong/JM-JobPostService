package rmit.saintgiong.jobpost.api.internal;

import rmit.saintgiong.jobpost.api.internal.dto.response.QueryJobPostResponseDto;

import java.util.List;

public interface QueryJobPostInterface {
    QueryJobPostResponseDto getJobPostById(String id);

    List<QueryJobPostResponseDto> getJobPostsByCompanyId(String companyId);
}
