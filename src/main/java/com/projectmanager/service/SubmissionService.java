package com.projectmanager.service;

import com.projectmanager.model.ProjectSubmission;
import com.projectmanager.model.User;
import com.projectmanager.repository.ProjectSubmissionRepository;
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
public class SubmissionService {

    private final ProjectSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    // Store files in /tmp/uploads (works on Render free tier)
    private final String uploadDir = "/tmp/uploads";

    public SubmissionService(ProjectSubmissionRepository submissionRepository,
                             UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    public void submit(String username, MultipartFile file, String githubLink, String description) throws IOException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectSubmission submission = new ProjectSubmission();
        submission.setSubmittedBy(user);
        submission.setGithubLink(githubLink);
        submission.setDescription(description);

        // Save file if provided
        if (file != null && !file.isEmpty()) {
            Files.createDirectories(Paths.get(uploadDir));
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path dest = Paths.get(uploadDir, uniqueName);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            submission.setFileName(file.getOriginalFilename());
            submission.setFilePath(dest.toString());
        }

        submissionRepository.save(submission);
    }

    public List<ProjectSubmission> getAllSubmissions() {
        return submissionRepository.findAllByOrderBySubmittedAtDesc();
    }

    public List<ProjectSubmission> getByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return submissionRepository.findBySubmittedByOrderBySubmittedAtDesc(user);
    }

    public ProjectSubmission getById(Long id) {
        return submissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Submission not found"));
    }

    public void delete(Long id) {
        submissionRepository.deleteById(id);
    }
}
