package rmit.saintgiong.jobpost.api.internal.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating an existing job post")
public class UpdateJobPostRequestDto {
    @NotBlank
    @Schema(description = "Title of the job post", example = "Lead Java Developer")
    private String title;

    @NotBlank
    @Schema(description = "Detailed description of the job", example = "Updated description...")
    private String description;

    @NotBlank
    @Schema(description = "City where the job is located", example = "Hanoi")
    private String city;

    @Schema(description = "Types of employment offered", example = "[\"PART_TIME\"]")
    private Set<String> employmentTypes;

    @Schema(description = "Title for the salary", example = "RANGE")
    private String salaryTitle;

    @Schema(description = "Minimum salary offered", example = "3000.0")
    private Double salaryMin;

    @Schema(description = "Maximum salary offered", example = "6000.0")
    private Double salaryMax;

    @Schema(description = "Set of skill tag IDs required for the job", example = "[4, 5]")
    private Set<Integer> skillTagIds;

    @NotNull
    @Schema(description = "Expiration date of the job post", example = "2026-01-01T00:00:00")
    private LocalDateTime expiryDate;

    @NotNull
    @JsonProperty("isPublished")
    @Schema(description = "Status of job post publication", example = "false")
    private boolean published;

    @NotBlank
    @Schema(description = "Country where the job is located", example = "Vietnam")
    private String country;

    @Schema(description = "ID of the company posting the job", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private String companyId;
}
