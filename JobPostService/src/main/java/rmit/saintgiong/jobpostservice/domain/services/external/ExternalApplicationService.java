package rmit.saintgiong.jobpostservice.domain.services.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rmit.saintgiong.jobpostapi.internal.common.dto.response.ApplicationResponseDto;
import rmit.saintgiong.jobpostapi.internal.common.type.JobApplicationStatus;
import rmit.saintgiong.jobpostservice.common.config.ExternalApplicationConfig;
import rmit.saintgiong.jobpostservice.domain.dto.external.ExternalApplicationResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApplicationService {

    private final ExternalApplicationConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<ApplicationResponseDto> getApplicationsByPostId(String postId) {
        String url = config.getServiceUrl() + "/applications/post/" + postId;
        log.info("Fetching applications for postId={} from {}", postId, url);

        try {
            ResponseEntity<List<ExternalApplicationResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ExternalApplicationResponseDto>>() {
                    });

            List<ExternalApplicationResponseDto> externalDtos = response.getBody();
            if (externalDtos != null && !externalDtos.isEmpty()) {
                log.info("Fetched {} applications for postId={}", externalDtos.size(), postId);
                return externalDtos.stream()
                        .map(this::mapToApplicationResponse)
                        .collect(Collectors.toList());
            } else {
                log.info("No applications found for postId={}", postId);
                return Collections.emptyList();
            }

        } catch (Exception e) {
            log.error("Failed to fetch applications for postId={}: {}", postId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetches the count of applications for a specific job post
     */
    public Integer getApplicationCount(String postId) {
        List<ApplicationResponseDto> applications = getApplicationsByPostId(postId);
        return applications.size();
    }

    /**
     * Maps external DTO to internal ApplicationResponseDto
     */
    private ApplicationResponseDto mapToApplicationResponse(ExternalApplicationResponseDto external) {
        if (external == null) {
            return null;
        }

        return ApplicationResponseDto.builder()
                .applicationId(external.applicationId())
                .postId(external.postId())
                .applicantId(external.applicantId())
                .fullName(external.fullName())
                .email(external.email())
                .phone(external.phone())
                .linkedIn(external.linkedIn())
                .employmentStatus(external.employmentStatus())
                .jobTitle(external.jobTitle())
                .salary(external.salary())
                .companyName(external.companyName())
                .companyLogo(external.companyLogo())
                .companyLocation(external.companyLocation())
                .cvUrl(external.cvUrl())
                .letterUrl(external.letterUrl())
                .status(parseStatus(external.status()))
                .createdAt(external.createdAt())
                .build();
    }

    private JobApplicationStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return JobApplicationStatus.PENDING;
        }
        try {
            return JobApplicationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown application status: {}, defaulting to PENDING", status);
            return JobApplicationStatus.PENDING;
        }
    }
}
