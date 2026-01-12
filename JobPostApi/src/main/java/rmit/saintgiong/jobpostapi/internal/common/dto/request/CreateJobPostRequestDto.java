package rmit.saintgiong.jobpostapi.internal.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rmit.saintgiong.jobpostapi.internal.common.type.SalaryTitle;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new job post")
public class CreateJobPostRequestDto {
    @NotBlank
    @Schema(description = "Title of the job post", example = "Senior Software Engineer")
    private String title;

    @NotBlank
    @Schema(description = "Detailed description of the job", example = "We are looking for...")
    private String description;

    @NotBlank
    @Schema(description = "City where the job is located", example = "Ho Chi Minh City")
    private String city;

    @Schema(description = "Types of employment offered", example = "[\"FULL_TIME\", \"PART_TIME\"]")
    private Set<String> employmentTypes;

    @Schema(description = "Title for the salary", example = "ABOUT")
    private SalaryTitle salaryTitle;

    @Schema(description = "Minimum salary offered", example = "2000.0")
    private Double salaryMin;

    @Schema(description = "Maximum salary offered", example = "5000.0")
    private Double salaryMax;

    @Schema(description = "Set of skill tag IDs required for the job", example = "[1, 2, 3]")
    private Set<Integer> skillTagIds;

    @NotNull
    @Schema(description = "Expiration date of the job post", example = "2025-12-31T23:59:59")
    private LocalDateTime expiryDate;

    @NotNull
    @JsonProperty("isPublished")
    @Schema(description = "Status of job post publication", example = "true")
    private boolean published;

    @NotBlank
    @Schema(description = "Country where the job is located", example = "Vietnam")
    private String country;

    @Schema(description = "ID of the company posting the job", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private String companyId;
}
