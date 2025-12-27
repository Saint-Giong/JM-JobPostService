package rmit.saintgiong.jobpostservice.domain.repositories.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "job_post_skill_tag")
@Getter
@Setter
public class JobPost_SkillTagEntity {

    @EmbeddedId
    private JobPostSkillTagId skillTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jobPostId")
    @JoinColumn(name = "job_post_id")
    private JobPostEntity jobPost;

    public JobPost_SkillTagEntity() {}

    public JobPost_SkillTagEntity(JobPostEntity jobPost, Integer tagId) {
        this.jobPost = jobPost;
        this.skillTagId = new JobPostSkillTagId(jobPost.getId(), tagId);
    }
}
