package rmit.saintgiong.jobpostservice.domain.repositories.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "postid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "salary_title")
    private String salaryTitle;

    @Column(name = "salary_min")
    private Double salaryMin;

    @Column(name = "salary_max")
    private Double salaryMax;

    @CreationTimestamp
    @Column(name = "posted_date", updatable = false)
    private LocalDateTime postedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_published")
    private boolean published;

    @Column(name = "country")
    private String country;

    @Column(name = "companyid")
    private UUID companyId;
}

