package com.projectmanager.service;

import com.projectmanager.model.Project;
import com.projectmanager.model.TeamMember;
import com.projectmanager.model.User;
import com.projectmanager.repository.ProjectRepository;
import com.projectmanager.repository.TeamMemberRepository;
import com.projectmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final String uploadDir = "/tmp/project-files";

    public ProjectService(ProjectRepository projectRepository,
                          TeamMemberRepository teamMemberRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByIdDesc();
    }

    public List<Project> getProjectsByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return projectRepository.findByOwnerOrderByIdDesc(user);
    }

    public Project getById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found: " + id));
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public Project saveWithFile(Project project, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            Files.createDirectories(Paths.get(uploadDir));
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path dest = Paths.get(uploadDir, uniqueName);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            project.setFileName(file.getOriginalFilename());
            project.setFilePath(dest.toString());
        }
        return projectRepository.save(project);
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public void addTeamMember(Long projectId, String name, String role, String email) {
        Project project = getById(projectId);
        TeamMember member = new TeamMember();
        member.setName(name);
        member.setRole(role);
        member.setEmail(email);
        member.setProject(project);
        teamMemberRepository.save(member);
    }

    public void removeTeamMember(Long memberId) {
        teamMemberRepository.deleteById(memberId);
    }

    public long countAll() {
        return projectRepository.count();
    }

    public long countByStatus(String status) {
        return projectRepository.countByStatus(status);
    }
}
