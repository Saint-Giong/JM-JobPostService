package rmit.saintgiong.jobpost.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rmit.saintgiong.jobpost.domain.repositories.entities.JobPostEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobPostRepository extends JpaRepository<JobPostEntity, UUID> {

}

