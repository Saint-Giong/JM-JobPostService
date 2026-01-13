package rmit.saintgiong.jobpostservice.domain.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalApplicationResponseDto(
        String applicationId,
        String postId,
        String applicantId,
        String fullName,
        String email,
        String phone,
        String linkedIn,
        String employmentStatus,
        String jobTitle,
        Double salary,
        String companyName,
        String companyLogo,
        String companyLocation,
        String cvUrl,
        String letterUrl,
        String status,
        LocalDateTime createdAt) {
}
