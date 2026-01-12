package rmit.saintgiong.jobpostapi.internal.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rmit.saintgiong.jobpostapi.internal.common.type.SalaryTitle;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response DTO for querying job post details")
public class QueryJobPostResponseDto {
    @Schema(description = "Unique identifier of the job post", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private String id;

    @Schema(description = "Title of the job post", example = "Software Architect")
    private String title;

    @Schema(description = "Detailed description of the job", example = "Responsible for designing...")
    private String description;

    @Schema(description = "City where the job is located", example = "Da Nang")
    private String city;

    @Schema(description = "Types of employment offered", example = "[\"CONTRACT\"]")
    private Set<String> employmentTypes;

    @Schema(description = "Set of skill tag IDs associated with the job", example = "[10, 11]")
    private Set<Integer> skillTagIds;

    @Schema(description = "Title for the salary", example = "Annual Package")
    private SalaryTitle salaryTitle;

    @Schema(description = "Minimum salary offered", example = "50000.0")
    private Double salaryMin;

    @Schema(description = "Maximum salary offered", example = "80000.0")
    private Double salaryMax;

    @Schema(description = "Date and time when the job was posted", example = "2024-01-01T10:00:00")
    private LocalDateTime postedDate;

    @Schema(description = "Expiration date of the job post", example = "2024-06-01T10:00:00")
    private LocalDateTime expiryDate;

    @Schema(description = "Status of job post publication", example = "true")
    private Boolean published;

    @Schema(description = "Country where the job is located", example = "Vietnam")
    private String country;

    private QueryCompanyProfileResponseDto company;
}
