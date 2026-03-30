package com.projectmanager.controller;

import com.projectmanager.model.Project;
import com.projectmanager.service.ProjectService;
import com.projectmanager.service.UserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final ProjectService projectService;
    private final UserService userService;

    public UserController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    // Dashboard - show only user's projects
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String username = auth.getName();
        List<Project> projects = projectService.getProjectsByUsername(username);
        long activeCount = projects.stream().filter(p -> "ACTIVE".equals(p.getStatus())).count();
        long completedCount = projects.stream().filter(p -> "COMPLETED".equals(p.getStatus())).count();
        long memberCount = projects.stream().mapToLong(p -> p.getTeamMembers() != null ? p.getTeamMembers().size() : 0).sum();
        model.addAttribute("projects", projects);
        model.addAttribute("username", username);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("memberCount", memberCount);
        return "user/dashboard";
    }

    // View project detail - only if it belongs to user
    @GetMapping("/project/{id}")
    public String viewProject(@PathVariable Long id, Authentication auth, Model model) {
        Project project = projectService.getById(id);
        // Security check - only show if project belongs to this user
        if (project.getOwner() == null || !project.getOwner().getUsername().equals(auth.getName())) {
            return "redirect:/user/dashboard";
        }
        model.addAttribute("project", project);
        return "user/project-detail";
    }

    // New project form
    @GetMapping("/project/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "user/project-form";
    }

    // Save new project
    @PostMapping("/project/save")
    public String saveProject(@ModelAttribute Project project,
                               @RequestParam(required = false) MultipartFile projectFile,
                               @RequestParam(required = false, defaultValue = "") String githubLink,
                               Authentication auth,
                               RedirectAttributes ra) {
        try {
            // Set owner to current logged-in user
            project.setOwner(userService.getUserByUsername(auth.getName()));
            if (githubLink != null && !githubLink.isEmpty()) {
                project.setGithubLink(githubLink);
            }
            projectService.saveWithFile(project, projectFile);
            ra.addFlashAttribute("successMsg", "Project created successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
        }
        return "redirect:/user/dashboard";
    }

    // Edit project form - only if it belongs to user
    @GetMapping("/project/edit/{id}")
    public String editProjectForm(@PathVariable Long id, Authentication auth, Model model) {
        Project project = projectService.getById(id);
        if (project.getOwner() == null || !project.getOwner().getUsername().equals(auth.getName())) {
            return "redirect:/user/dashboard";
        }
        model.addAttribute("project", project);
        model.addAttribute("isNew", false);
        return "user/project-form";
    }

    // Update project - only if it belongs to user
    @PostMapping("/project/update")
    public String updateProject(@ModelAttribute Project project,
                                 @RequestParam(required = false) MultipartFile projectFile,
                                 @RequestParam(required = false, defaultValue = "") String githubLink,
                                 Authentication auth,
                                 RedirectAttributes ra) {
        try {
            Project existing = projectService.getById(project.getId());
            if (existing.getOwner() == null || !existing.getOwner().getUsername().equals(auth.getName())) {
                return "redirect:/user/dashboard";
            }
            project.setOwner(existing.getOwner());
            if (githubLink != null && !githubLink.isEmpty()) {
                project.setGithubLink(githubLink);
            } else {
                project.setGithubLink(existing.getGithubLink());
            }
            // Keep existing file if no new file uploaded
            if (projectFile == null || projectFile.isEmpty()) {
                project.setFileName(existing.getFileName());
                project.setFilePath(existing.getFilePath());
            }
            projectService.saveWithFile(project, projectFile);
            ra.addFlashAttribute("successMsg", "Project updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
        }
        return "redirect:/user/dashboard";
    }

    // Delete project - only if it belongs to user
    @GetMapping("/project/delete/{id}")
    public String deleteProject(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Project project = projectService.getById(id);
        if (project.getOwner() != null && project.getOwner().getUsername().equals(auth.getName())) {
            projectService.delete(id);
            ra.addFlashAttribute("successMsg", "Project deleted.");
        }
        return "redirect:/user/dashboard";
    }

    // Download project file
    @GetMapping("/project/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, Authentication auth) {
        Project project = projectService.getById(id);
        if (project.getOwner() == null || !project.getOwner().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (project.getFilePath() == null) return ResponseEntity.notFound().build();
        File file = new File(project.getFilePath());
        if (!file.exists()) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + project.getFileName() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }
}
