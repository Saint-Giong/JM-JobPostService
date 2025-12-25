package rmit.saintgiong.jobpost.domain.repositories.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;



@Entity
@Table(name = "job_post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "skillTags")
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
    private BitSet employmentType;

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

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JobPost_SkillTagEntity> skillTags;

    public void addSkillTag(Integer tagId) {
        JobPost_SkillTagEntity skillTagEntity = new JobPost_SkillTagEntity(this, tagId);

        if (this.skillTags == null) {
            this.skillTags = new HashSet<>();
        }

        this.skillTags.add(skillTagEntity);
        skillTagEntity.setJobPost(this);
    }
}

