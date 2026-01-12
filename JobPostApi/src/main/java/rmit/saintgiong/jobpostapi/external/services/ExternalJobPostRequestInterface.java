package rmit.saintgiong.jobpostapi.external.services;


import rmit.saintgiong.jobpostapi.internal.common.dto.response.QueryCompanyProfileResponseDto;

import java.util.List;
import java.util.UUID;

public interface ExternalJobPostRequestInterface {
    QueryCompanyProfileResponseDto sendGetProfileRequest(UUID companyId);
    List<QueryCompanyProfileResponseDto> sendGetAllProfilesRequest();


}
