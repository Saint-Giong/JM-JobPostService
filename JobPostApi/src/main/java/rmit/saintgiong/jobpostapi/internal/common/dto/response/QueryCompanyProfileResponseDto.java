package rmit.saintgiong.jobpostapi.internal.common.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryCompanyProfileResponseDto {
    private String id;
    private String name;
    private String phone;
    private String address;
    private String city;
    private String aboutUs;
    private String admissionDescription;
    private String logoUrl;
    private String country;
}

