package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.service.ExamService;
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
public class ExamViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamViewController.class);

    @Autowired
    private ExamService examService;

    @Autowired
    private UserService userService;

    @GetMapping("/exams")
    public String listExams(Model model, Authentication authentication) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("user", user);
            
            if (user.getRole() == User.UserRole.COMPANY) {
                model.addAttribute("exams", examService.getExamsByCompany(user));
                return "exam/company-exams";
            } else {
                model.addAttribute("activeExams", examService.getActiveExams());
                return "exam/candidate-exams";
            }
        } catch (Exception e) {
            logger.error("Error loading exams: {}", e.getMessage());
            model.addAttribute("error", "Failed to load exams");
            return "error/500";
        }
    }

    @GetMapping("/exams/create")
    public String createExamForm(Model model, Authentication authentication) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            if (user.getRole() != User.UserRole.COMPANY) {
                return "error/403";
            }
            model.addAttribute("user", user);
            return "exam/create";
        } catch (Exception e) {
            logger.error("Error loading exam creation form: {}", e.getMessage());
            model.addAttribute("error", "Failed to load exam creation form");
            return "error/500";
        }
    }

    @GetMapping("/exams/{id}/edit")
    public String editExamForm(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            if (user.getRole() != User.UserRole.COMPANY) {
                return "error/403";
            }
            model.addAttribute("user", user);
            // Add exam loading logic here
            return "exam/edit";
        } catch (Exception e) {
            logger.error("Error loading exam edit form: {}", e.getMessage());
            model.addAttribute("error", "Failed to load exam edit form");
            return "error/500";
        }
    }
} 