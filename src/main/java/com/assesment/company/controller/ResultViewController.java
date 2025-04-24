package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.service.ResultService;
import com.assesment.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ResultViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ResultViewController.class);

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    @GetMapping("/results")
    public String listResults(Model model, Authentication authentication) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("user", user);
            model.addAttribute("results", resultService.getRecentResults(user));
            
            if (user.getRole() == User.UserRole.COMPANY) {
                return "result/company-results";
            } else {
                return "result/candidate-results";
            }
        } catch (Exception e) {
            logger.error("Error loading results: {}", e.getMessage());
            model.addAttribute("error", "Failed to load results");
            return "error/500";
        }
    }

    @GetMapping("/results/{id}")
    public String viewResult(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("user", user);
            // Add result loading logic here
            return "result/view";
        } catch (Exception e) {
            logger.error("Error loading result details: {}", e.getMessage());
            model.addAttribute("error", "Failed to load result details");
            return "error/500";
        }
    }
} 