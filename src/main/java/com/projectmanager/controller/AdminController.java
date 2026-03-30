package com.projectmanager.controller;

import com.projectmanager.model.Project;
import com.projectmanager.model.User;
import com.projectmanager.service.ProjectService;
import com.projectmanager.service.UserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProjectService projectService;
    private final UserService userService;

    public AdminController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("totalProjects", projectService.countAll());
        model.addAttribute("activeProjects", projectService.countByStatus("ACTIVE"));
        model.addAttribute("completedProjects", projectService.countByStatus("COMPLETED"));
        model.addAttribute("totalUsers", userService.countUsers());
        return "admin/dashboard";
    }

    @GetMapping("/projects")
    public String listProjects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        return "admin/project-list";
    }

    @GetMapping("/project/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("users", userService.getAllNonAdminUsers());
        model.addAttribute("isNew", true);
        return "admin/project-form";
    }

    @GetMapping("/project/edit/{id}")
    public String editProjectForm(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectService.getById(id));
        model.addAttribute("users", userService.getAllNonAdminUsers());
        model.addAttribute("isNew", false);
        return "admin/project-form";
    }

    @PostMapping("/project/save")
    public String saveProject(@ModelAttribute Project project,
                               @RequestParam(required = false) Long ownerId,
                               @RequestParam(required = false) MultipartFile projectFile,
                               @RequestParam(required = false, defaultValue = "") String githubLink,
                               RedirectAttributes ra) {
        try {
            if (ownerId != null) {
                User owner = userService.getUserById(ownerId);
                project.setOwner(owner);
            }
            if (githubLink != null && !githubLink.isEmpty()) {
                project.setGithubLink(githubLink);
            }
            projectService.saveWithFile(project, projectFile);
            ra.addFlashAttribute("successMsg", "Project saved successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Error saving project: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/project/delete/{id}")
    public String deleteProject(@PathVariable Long id, RedirectAttributes ra) {
        projectService.delete(id);
        ra.addFlashAttribute("successMsg", "Project deleted.");
        return "redirect:/admin/dashboard";
    }

    // Download project file
    @GetMapping("/project/{id}/download")
    public ResponseEntity<Resource> downloadProjectFile(@PathVariable Long id) {
        Project project = projectService.getById(id);
        if (project.getFilePath() == null) return ResponseEntity.notFound().build();
        File file = new File(project.getFilePath());
        if (!file.exists()) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + project.getFileName() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

    @PostMapping("/project/{projectId}/member/add")
    public String addMember(@PathVariable Long projectId,
                            @RequestParam String name,
                            @RequestParam String role,
                            @RequestParam(required = false, defaultValue = "") String email,
                            RedirectAttributes ra) {
        projectService.addTeamMember(projectId, name, role, email);
        ra.addFlashAttribute("successMsg", "Team member added!");
        return "redirect:/admin/project/edit/" + projectId;
    }

    @GetMapping("/member/{memberId}/delete/project/{projectId}")
    public String removeMember(@PathVariable Long memberId,
                               @PathVariable Long projectId,
                               RedirectAttributes ra) {
        projectService.removeTeamMember(memberId);
        ra.addFlashAttribute("successMsg", "Team member removed.");
        return "redirect:/admin/project/edit/" + projectId;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user-list";
    }

    @GetMapping("/user/new")
    public String newUserForm() {
        return "admin/user-form";
    }

    @PostMapping("/user/save")
    public String saveUser(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role,
                           @RequestParam(required = false, defaultValue = "") String email,
                           @RequestParam(required = false, defaultValue = "") String fullName,
                           RedirectAttributes ra) {
        try {
            userService.createUser(username, password, role, email, fullName);
            ra.addFlashAttribute("successMsg", "User created successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("successMsg", "User deleted.");
        return "redirect:/admin/users";
    }
}
