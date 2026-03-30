package com.projectmanager.controller;

import com.projectmanager.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String doSignup(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam(required = false, defaultValue = "") String fullName,
                           @RequestParam(required = false, defaultValue = "") String email,
                           Model model,
                           RedirectAttributes ra) {

        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMsg", "Passwords do not match.");
            model.addAttribute("username", username);
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            return "signup";
        }

        // Validate password length
        if (password.length() < 6) {
            model.addAttribute("errorMsg", "Password must be at least 6 characters.");
            model.addAttribute("username", username);
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            return "signup";
        }

        try {
            // All self-registered users get USER role
            userService.createUser(username, password, "USER", email, fullName);
            ra.addFlashAttribute("registered", true);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Username '" + username + "' is already taken. Please choose another.");
            model.addAttribute("username", "");
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            return "signup";
        }
    }

    // Smart redirect after login based on role
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth == null) return "redirect:/login";
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
    }
}
