package com.projectmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String fullName;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Project> projects;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final User user = new User();
        public Builder username(String v) { user.username = v; return this; }
        public Builder password(String v) { user.password = v; return this; }
        public Builder role(String v) { user.role = v; return this; }
        public Builder email(String v) { user.email = v; return this; }
        public Builder fullName(String v) { user.fullName = v; return this; }
        public User build() { return user; }
    }
}
