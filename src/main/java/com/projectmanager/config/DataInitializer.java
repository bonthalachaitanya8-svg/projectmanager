package com.projectmanager.config;

import com.projectmanager.model.User;
import com.projectmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Only create admin if no users exist
        if (userRepository.count() > 0) {
            System.out.println("=== Database already initialized, skipping ===");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        admin.setEmail("admin@company.com");
        admin.setFullName("System Administrator");
        userRepository.save(admin);

        System.out.println("=== Admin user created! Login: admin/admin123 ===");
    }
}
