package rmit.saintgiong.jobpostservice.common.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rmit.saintgiong.jobpostapi.internal.common.dto.request.CreateJobPostRequestDto;
import rmit.saintgiong.jobpostapi.internal.common.type.SalaryTitle;
import rmit.saintgiong.jobpostservice.domain.mappers.JobPostMapper;
import rmit.saintgiong.jobpostservice.domain.repositories.JobPostRepository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;


@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeedingConfig implements CommandLineRunner {
    private final JobPostRepository jobPostRepository;
    private final JobPostMapper jobPostMapper;

    // Company UUIDs - must match Auth service
    private static final String NAB_COMPANY_ID = "11111111-1111-1111-1111-111111111111";
    private static final String GOOGLE_COMPANY_ID = "22222222-2222-2222-2222-222222222222";
    private static final String NETCOMPANY_COMPANY_ID = "33333333-3333-3333-3333-333333333333";
    private static final String SHOPEE_COMPANY_ID = "44444444-4444-4444-4444-444444444444";

    // Skill Tag IDs - must match SkillTag service (1-indexed based on DEFAULT_SKILL_TAGS order)
    private static final int TAG_JAVA = 1;          // JAVA
    private static final int TAG_PYTHON = 2;        // PYTHON
    private static final int TAG_JAVASCRIPT = 3;    // JAVASCRIPT
    private static final int TAG_TYPESCRIPT = 4;    // TYPESCRIPT
    private static final int TAG_REACT = 5;         // REACT
    private static final int TAG_ANGULAR = 6;       // ANGULAR
    private static final int TAG_VUE = 7;           // VUE
    private static final int TAG_SPRING_BOOT = 8;   // SPRING BOOT
    private static final int TAG_NODE_JS = 9;       // NODE.JS
    private static final int TAG_SNOWFLAKE = 10;    // SNOWFLAKE
    private static final int TAG_MONGODB = 11;      // MONGODB
    private static final int TAG_DOCKER = 12;       // DOCKER
    private static final int TAG_KUBERNETES = 13;   // KUBERNETES
    private static final int TAG_AWS = 14;          // AWS
    private static final int TAG_AZURE = 15;        // AZURE
    private static final int TAG_COMMUNICATION = 16; // COMMUNICATION
    private static final int TAG_REST_API = 17;     // REST API
    private static final int TAG_GRAPHQL = 18;      // GRAPHQL
    private static final int TAG_HTML = 19;         // HTML
    private static final int TAG_CSS = 20;          // CSS

    @Override
    public void run(String... args) {
        if (jobPostRepository.count() > 0) {
            return;
        }

        log.info("Seeding Job Posts for 4 companies (2 Freemiums + 2 Premiums)...");

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);

        List<CreateJobPostRequestDto> dtos = List.of(
                // ============================================================
                // FREEMIUM 1: NAB - 2 Job Posts (Financial/Banking sector)
                // ============================================================
                CreateJobPostRequestDto.builder()
                        .companyId(NAB_COMPANY_ID)
                        .title("FinTech Software Developer")
                        .description("Join NAB's digital transformation team to build innovative banking solutions. You will work on core banking systems, payment processing, and customer-facing applications using Java and Spring Boot.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(1500.0)
                        .salaryMax(2500.0)
                        .skillTagIds(Set.of(TAG_JAVA, TAG_SPRING_BOOT, TAG_REST_API))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId(NAB_COMPANY_ID)
                        .title("Cloud Infrastructure Engineer")
                        .description("Design and maintain NAB's cloud infrastructure on AWS. Responsible for CI/CD pipelines, container orchestration, and infrastructure as code.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(2000.0)
                        .salaryMax(3000.0)
                        .skillTagIds(Set.of(TAG_AWS, TAG_DOCKER, TAG_KUBERNETES))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                // ============================================================
                // FREEMIUM 2: Google Vietnam - 2 Job Posts (Technology sector)
                // ============================================================
                CreateJobPostRequestDto.builder()
                        .companyId(GOOGLE_COMPANY_ID)
                        .title("Frontend Engineer")
                        .description("Build beautiful and performant web applications using React and TypeScript. Work on Google's internal tools and customer-facing products.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(2500.0)
                        .salaryMax(4000.0)
                        .skillTagIds(Set.of(TAG_REACT, TAG_TYPESCRIPT, TAG_HTML, TAG_CSS))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId(GOOGLE_COMPANY_ID)
                        .title("Machine Learning Engineer")
                        .description("Develop and deploy ML models at scale. Work on cutting-edge AI/ML projects including natural language processing and computer vision.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(3500.0)
                        .salaryMax(5500.0)
                        .skillTagIds(Set.of(TAG_PYTHON, TAG_AWS, TAG_DOCKER))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                // ============================================================
                // PREMIUM 1: Netcompany - Software Engineering Domain
                // 2+ Job Posts hiring Software Engineers
                // Search Profile: React, Spring Boot, Docker talents
                // ============================================================
                CreateJobPostRequestDto.builder()
                        .companyId(NETCOMPANY_COMPANY_ID)
                        .title("Full-stack Software Engineer Intern")
                        .description("Join our international team to build large-scale enterprise solutions using React and Spring Boot. Great opportunity for fresh graduates to learn from senior engineers. Work with Docker containerization and modern CI/CD practices.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME", "INTERNSHIP"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(850.0)
                        .salaryMax(1200.0)
                        .skillTagIds(Set.of(TAG_REACT, TAG_SPRING_BOOT, TAG_DOCKER))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId(NETCOMPANY_COMPANY_ID)
                        .title("Senior Java Backend Developer")
                        .description("Lead developer role for enterprise banking and government solutions. Architect microservices using Spring Boot and deploy with Docker/Kubernetes.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(2500.0)
                        .salaryMax(4000.0)
                        .skillTagIds(Set.of(TAG_JAVA, TAG_SPRING_BOOT, TAG_DOCKER, TAG_KUBERNETES))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId(NETCOMPANY_COMPANY_ID)
                        .title("React Frontend Developer")
                        .description("Build responsive and accessible web applications using React. Work closely with UX designers and backend teams to deliver exceptional user experiences.")
                        .city("Ho Chi Minh City")
                        .country("Vietnam")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(1800.0)
                        .salaryMax(2800.0)
                        .skillTagIds(Set.of(TAG_REACT, TAG_TYPESCRIPT, TAG_HTML, TAG_CSS))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                // ============================================================
                // PREMIUM 2: Shopee Singapore - Data Engineering Domain
                // 2+ Job Posts hiring Data Engineers
                // Search Profile: Python, AWS, Snowflake talents
                // ============================================================
                CreateJobPostRequestDto.builder()
                        .companyId(SHOPEE_COMPANY_ID)
                        .title("Senior Data Engineer (Contract)")
                        .description("Build and maintain scalable data pipelines using Python and AWS. Work with Snowflake data warehouse to power real-time analytics for e-commerce operations. Contract position with potential for conversion.")
                        .city("Singapore")
                        .country("Singapore")
                        .employmentTypes(Set.of("CONTRACT"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(4000.0)
                        .salaryMax(6000.0)
                        .skillTagIds(Set.of(TAG_PYTHON, TAG_AWS, TAG_SNOWFLAKE))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId(SHOPEE_COMPANY_ID)
                        .title("Data Platform Engineer")
                        .description("Design and implement data infrastructure on AWS. Build ETL pipelines, manage Snowflake data warehouse, and ensure data quality across the organization.")
                        .city("Singapore")
                        .country("Singapore")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(5000.0)
                        .salaryMax(7500.0)
                        .skillTagIds(Set.of(TAG_PYTHON, TAG_AWS, TAG_SNOWFLAKE, TAG_DOCKER))
                        .expiryDate(expiryDate)
                        .published(true)
                        .build(),

                CreateJobPostRequestDto.builder()
                        .companyId(SHOPEE_COMPANY_ID)
                        .title("Analytics Engineer")
                        .description("Transform raw data into actionable insights using Python and SQL. Work with Snowflake to build data models and dashboards for business stakeholders.")
                        .city("Singapore")
                        .country("Singapore")
                        .employmentTypes(Set.of("FULL_TIME"))
                        .salaryTitle(SalaryTitle.RANGE)
                        .salaryMin(4500.0)
                        .salaryMax(6500.0)
                        .skillTagIds(Set.of(TAG_PYTHON, TAG_SNOWFLAKE))
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

        log.info("Seeded {} Job Posts (NAB: 2, Google: 2, Netcompany: 3, Shopee: 3).", dtos.size());
    }
}