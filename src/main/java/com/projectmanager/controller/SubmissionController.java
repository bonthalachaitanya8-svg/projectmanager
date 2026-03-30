package com.projectmanager.controller;

import com.projectmanager.model.ProjectSubmission;
import com.projectmanager.service.SubmissionService;
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

@Controller
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // User - submit page
    @GetMapping("/user/submit")
    public String submitPage(Authentication auth, Model model) {
        model.addAttribute("submissions", submissionService.getByUsername(auth.getName()));
        return "user/submit";
    }

    // User - handle submission
    @PostMapping("/user/submit")
    public String handleSubmit(@RequestParam(required = false) MultipartFile file,
                               @RequestParam(required = false, defaultValue = "") String githubLink,
                               @RequestParam(required = false, defaultValue = "") String description,
                               Authentication auth,
                               RedirectAttributes ra) {
        try {
            submissionService.submit(auth.getName(), file, githubLink, description);
            ra.addFlashAttribute("successMsg", "Submitted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Submission failed: " + e.getMessage());
        }
        return "redirect:/user/submit";
    }

    // Admin - view all submissions
    @GetMapping("/admin/submissions")
    public String adminSubmissions(Model model) {
        model.addAttribute("submissions", submissionService.getAllSubmissions());
        return "admin/submissions";
    }

    // Admin - download file
    @GetMapping("/admin/submission/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        ProjectSubmission sub = submissionService.getById(id);
        if (sub.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(sub.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sub.getFileName() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

    // Admin - delete submission
    @GetMapping("/admin/submission/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        submissionService.delete(id);
        ra.addFlashAttribute("successMsg", "Submission deleted.");
        return "redirect:/admin/submissions";
    }
}
