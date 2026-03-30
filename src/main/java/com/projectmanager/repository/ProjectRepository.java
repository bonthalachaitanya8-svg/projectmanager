package com.projectmanager.repository;

import com.projectmanager.model.Project;
import com.projectmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(User owner);
    List<Project> findByOwnerOrderByIdDesc(User owner);
    List<Project> findAllByOrderByIdDesc();

    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status")
    long countByStatus(String status);
}
