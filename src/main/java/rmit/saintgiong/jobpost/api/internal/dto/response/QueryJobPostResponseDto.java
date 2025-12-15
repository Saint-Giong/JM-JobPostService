package rmit.saintgiong.jobpost.api.internal.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QueryJobPostResponseDto {
    private String id;
    private String title;
    private String description;
    private String city;
    private String employmentType;
    private String salaryTitle;
    private Double salaryMin;
    private Double salaryMax;
    private LocalDateTime postedDate;
    private LocalDateTime expiryDate;
    private Boolean published;
    private String country;
    private String companyId;
}
