package com.projectmanager.repository;

import com.projectmanager.model.ProjectSubmission;
import com.projectmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, Long> {
    List<ProjectSubmission> findBySubmittedByOrderBySubmittedAtDesc(User user);
    List<ProjectSubmission> findAllByOrderBySubmittedAtDesc();
}
