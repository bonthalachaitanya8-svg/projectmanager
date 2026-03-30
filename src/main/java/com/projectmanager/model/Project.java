package com.projectmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String briefing;

    @Column(length = 50)
    private String status;

    @Column(length = 50)
    private String priority;

    private LocalDate startDate;
    private LocalDate endDate;

    // New fields
    @Column(length = 500)
    private String githubLink;

    @Column(length = 500)
    private String fileName;

    @Column(length = 500)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TeamMember> teamMembers;

    public Project() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBriefing() { return briefing; }
    public void setBriefing(String briefing) { this.briefing = briefing; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public List<TeamMember> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<TeamMember> t) { this.teamMembers = t; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Project p = new Project();
        public Builder name(String v) { p.name = v; return this; }
        public Builder description(String v) { p.description = v; return this; }
        public Builder briefing(String v) { p.briefing = v; return this; }
        public Builder status(String v) { p.status = v; return this; }
        public Builder priority(String v) { p.priority = v; return this; }
        public Builder startDate(LocalDate v) { p.startDate = v; return this; }
        public Builder endDate(LocalDate v) { p.endDate = v; return this; }
        public Builder owner(User v) { p.owner = v; return this; }
        public Project build() { return p; }
    }
}
