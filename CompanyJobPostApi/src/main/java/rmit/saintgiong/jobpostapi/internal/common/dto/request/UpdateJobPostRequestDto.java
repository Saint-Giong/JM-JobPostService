package rmit.saintgiong.jobpostapi.internal.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UpdateJobPostRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String city;

    private String employmentType;

    private String salaryTitle;

    private Double salaryMin;

    private Double salaryMax;

    @NotNull
    private LocalDateTime expiryDate;

    @NotNull
    @JsonProperty("isPublished")
    private boolean published;

    @NotBlank
    private String country;

    private String companyId;
}
