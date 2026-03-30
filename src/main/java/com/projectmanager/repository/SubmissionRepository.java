package com.projectmanager.repository;

import com.projectmanager.model.Submission;
import com.projectmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findBySubmittedByOrderBySubmittedAtDesc(User user);
    List<Submission> findAllByOrderBySubmittedAtDesc();
}
