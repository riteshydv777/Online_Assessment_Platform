package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        logger.debug("Accessing login page");
        model.addAttribute("title", "Login - Assessment Platform");
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.debug("Accessing registration page");
        model.addAttribute("title", "Register - Assessment Platform");
        model.addAttribute("user", new User());
        logger.debug("Registration page model prepared with title and empty user");
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        logger.info("Attempting to register user with email: {}", user.getEmail());
        
        // Validate password confirmation
        String confirmPassword = user.getConfirmPassword();
        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.user", "Passwords do not match");
        }

        if (result.hasErrors()) {
            logger.warn("Registration validation failed for email: {}", user.getEmail());
            model.addAttribute("title", "Register - Assessment Platform");
            return "auth/register";
        }

        try {
            // Set default values for new user
            user.setEnabled(true);
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            
            // Set name to username if not provided
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                user.setName(user.getUsername());
            }
            
            // Set username to email if not provided
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                user.setUsername(user.getEmail());
            }
            
            // Validate role
            if (user.getRole() == null) {
                logger.warn("No role selected for user: {}", user.getEmail());
                result.rejectValue("role", "error.user", "Please select an account type");
                model.addAttribute("title", "Register - Assessment Platform");
                return "auth/register";
            }
            
            userService.registerUser(user);
            logger.info("Successfully registered user with email: {} as {}", user.getEmail(), user.getRole());
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Registration failed for email: {}. Error: {}", user.getEmail(), e.getMessage());
            result.rejectValue("email", "error.user", "An account already exists for this email.");
            model.addAttribute("title", "Register - Assessment Platform");
            return "auth/register";
        }
    }

    @GetMapping("/auth/login")
    public String loginAuth(Model model) {
        logger.debug("Redirecting /auth/login to /login");
        return "redirect:/login";
    }
}

