package rmit.saintgiong.jobpostservice.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.type.SalaryTitle;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeedingConfig implements CommandLineRunner {
    private final JobPostRepository jobPostRepository;
    private final JobPostMapper jobPostMapper;


    private static final int TAG_JAVA = 1;
    private static final int TAG_PYTHON = 2;
    private static final int TAG_REACT = 5;
    private static final int TAG_SPRING_BOOT = 8;
    private static final int TAG_SNOWFLAKE = 10;
    private static final int TAG_DOCKER = 12;
    private static final int TAG_AWS = 14;
    private static final int TAG_COMMUNICATION = 16;
    private static final int TAG_HTML = 19;

    @Override
    public void run(String... args) {
        if (jobPostRepository.count() > 0) {
            return;
        }

        log.info("Seeding Job Posts with matched Tag IDs...");

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);

        List<CreateJobPostRequestDto> dtos = List.of(

                CreateJobPostRequestDto.builder()
                        .companyId("33333333-3333-3333-3333-333333333333")
                        .title("Full-stack Software Engineer Intern")
                        .description("Join our international team to build large-scale enterprise solutions using React and Spring Boot.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME", "INTERNSHIP"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(850.0)
                        .salaryMax(1200.0)
                        // Correctly mapped IDs: React(5), Spring Boot(8), Docker(12)
                        .skillTagIds(Set.of(TAG_REACT, TAG_SPRING_BOOT, TAG_DOCKER))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId("33333333-3333-3333-3333-333333333333")
                        .title("Senior Java Backend Developer")
                        .description("Lead developer role for banking solutions.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.ESTIMATION)
                        .salaryMin(2000.0)
                        .salaryMax(2000.0)
                        // Java(1), Spring Boot(8), SQL(10)
                        .skillTagIds(Set.of(TAG_JAVA, TAG_SPRING_BOOT, TAG_SNOWFLAKE))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId("22222222-2222-2222-2222-222222222222")
                        .title("Data Engineer (Contract)")
                        .description("Build scalable data pipelines on Cloud infrastructure.")
                        .city("Singapore")
                        .country("Singapore")
                        .employmentTypes(Set.of("CONTRACT"))
                        .salaryTitle(SalaryTitle.NEGOTIABLE)
                        .salaryMin(4000.0)
                        .salaryMax(4000.0)
                        .skillTagIds(Set.of(TAG_PYTHON, TAG_AWS, TAG_SNOWFLAKE))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId("22222222-2222-2222-2222-222222222222")
                        .title("AI Research Scientist")
                        .description("Research LLM optimization techniques.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(3000.0)
                        .salaryMax(5000.0)
                        // Python(2), SQL(10)
                        .skillTagIds(Set.of(TAG_PYTHON, TAG_SNOWFLAKE))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId("44444444-4444-4444-4444-444444444444")
                        .title("Technical Customer Service Lead")
                        .description("Manage CX team for Shopee Express. Technical background preferred.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(600.0)
                        .salaryMax(900.0)
                        .skillTagIds(Set.of(TAG_SNOWFLAKE, TAG_COMMUNICATION))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId("44444444-4444-4444-4444-444444444444")
                        .title("Logistics Data Coordinator")
                        .description("Warehouse operations oversight and data entry.")
                        .city("Hanoi")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.ESTIMATION)
                        .salaryMin(500.0)
                        .salaryMax(500.0)
                        .skillTagIds(Set.of(TAG_SNOWFLAKE))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId("55555555-5555-5555-5555-555555555555")
                        .title("Digital Store Manager")
                        .description("Manage flagship store operations and digital POS systems.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.NEGOTIABLE)
                        .salaryMin(700.0)
                        .salaryMax(700.0)
                        // Basic tech skill: HTML(19) or SQL(10)
                        .skillTagIds(Set.of(TAG_HTML))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId("55555555-5555-5555-5555-555555555555")
                        .title("IT Support Staff")
                        .description("Internal IT helpdesk support.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME", "INTERNSHIP"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(400.0)
                        .salaryMax(600.0)
                        .skillTagIds(Set.of(TAG_JAVA, TAG_HTML))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build()
        );

        // Process DTOs -> Entities
        for (CreateJobPostRequestDto dto : dtos) {
            JobPostEntity entity = jobPostMapper.fromCreateCommand(dto);

            if (dto.getSkillTagIds() != null) {
                for (Integer tagId : dto.getSkillTagIds()) {
                    entity.addSkillTag(tagId);
                }
            }
            jobPostRepository.save(entity);
        }

        log.info("Seeded {} Job Posts.", dtos.size());
    }
}