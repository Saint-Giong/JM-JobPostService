package rmit.saintgiong.jobpostservice.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rmit.saintgiong.jobpostservice.domain.repositories.entities.JobPostEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobPostRepository extends JpaRepository<JobPostEntity, UUID> {

    List<JobPostEntity> findAllByCompanyId(UUID companyId);

}
