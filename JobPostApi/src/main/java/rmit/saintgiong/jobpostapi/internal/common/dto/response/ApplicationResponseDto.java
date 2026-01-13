package rmit.saintgiong.jobpostapi.internal.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rmit.saintgiong.jobpostapi.internal.common.type.JobApplicationStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response DTO for application details")
public class ApplicationResponseDto {
    @Schema(description = "Unique identifier of the application", example = "0a3621b2-1487-4e56-8c6c-2858c251b0c1")
    private String applicationId;

    @Schema(description = "Job post ID this application belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
    private String postId;

    @Schema(description = "Applicant's unique identifier", example = "660e8400-e29b-41d4-a716-446655440001")
    private String applicantId;

    @Schema(description = "Full name of the applicant", example = "John Doe")
    private String fullName;

    @Schema(description = "Email of the applicant", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number of the applicant", example = "1234567890")
    private String phone;

    @Schema(description = "LinkedIn profile URL", example = "https://linkedin.com/in/johndoe")
    private String linkedIn;

    @Schema(description = "Current employment status", example = "EMPLOYED")
    private String employmentStatus;

    @Schema(description = "Current job title", example = "Software Engineer")
    private String jobTitle;

    @Schema(description = "Current salary", example = "80000.0")
    private Double salary;

    @Schema(description = "Current company name", example = "Tech Corp")
    private String companyName;

    @Schema(description = "Current company logo URL", example = "https://example.com/logo.png")
    private String companyLogo;

    @Schema(description = "Current company location", example = "Ho Chi Minh City")
    private String companyLocation;

    @Schema(description = "CV/Resume URL", example = "https://example.com/cv.pdf")
    private String cvUrl;

    @Schema(description = "Cover letter URL", example = "https://example.com/letter.pdf")
    private String letterUrl;

    @Schema(description = "Status of the application", example = "SUBMITTED")
    private JobApplicationStatus status;

    @Schema(description = "Date when the application was created", example = "2026-01-13T12:19:05.136658")
    private LocalDateTime createdAt;
}
