package rmit.saintgiong.jobpostservice.domain.repositories.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class JobPostSkillTagId implements Serializable {

    @Column(name = "job_post_id")
    private UUID jobPostId;

    @Column(name = "tag_id")
    private Integer tagId;

    public JobPostSkillTagId() {}

    public JobPostSkillTagId(UUID jobPostId, Integer tagId) {
        this.jobPostId = jobPostId;
        this.tagId = tagId;
    }

    public UUID getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(UUID jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobPostSkillTagId that = (JobPostSkillTagId) o;
        return Objects.equals(jobPostId, that.jobPostId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobPostId, tagId);
    }
}
