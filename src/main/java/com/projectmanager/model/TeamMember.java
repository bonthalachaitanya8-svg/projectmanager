package com.projectmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String role;

    @Column(length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public TeamMember() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final TeamMember m = new TeamMember();
        public Builder name(String v) { m.name = v; return this; }
        public Builder role(String v) { m.role = v; return this; }
        public Builder email(String v) { m.email = v; return this; }
        public Builder project(Project v) { m.project = v; return this; }
        public TeamMember build() { return m; }
    }
}
