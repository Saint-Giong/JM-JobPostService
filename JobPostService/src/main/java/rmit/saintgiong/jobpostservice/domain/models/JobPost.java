package rmit.saintgiong.jobpostservice.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class JobPost {
    private UUID id;

    private String title;

    private String description;

    private String city;

    private String employmentType;

    private String salaryTitle;

    private Double salaryMin;

    private Double salaryMax;

    private LocalDateTime postedDate;

    private LocalDateTime expiryDate;

    private boolean published;

    private String country;

    private UUID companyId;
}
