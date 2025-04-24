package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ProfileController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("user", user);
            return "profile/view";
        } catch (Exception e) {
            logger.error("Error loading profile: {}", e.getMessage());
            model.addAttribute("error", "Failed to load profile");
            return "error/500";
        }
    }
} 