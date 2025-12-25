package rmit.saintgiong.jobpost.api.internal.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class QueryJobPostResponseDto {
    private String id;
    private String title;
    private String description;
    private String city;
    private Set<String> employmentTypes;
    private Set<Integer> skillTagIds;
    private String salaryTitle;
    private Double salaryMin;
    private Double salaryMax;
    private LocalDateTime postedDate;
    private LocalDateTime expiryDate;
    private Boolean published;
    private String country;
    private String companyId;
}
