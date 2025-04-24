package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.entity.Exam;
import com.assesment.company.entity.Result;
import com.assesment.company.service.ExamService;
import com.assesment.company.service.ResultService;
import com.assesment.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.HashMap;
import java.util.Map;
import com.assesment.company.repository.ResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;

@Controller
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private ExamService examService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResultRepository resultRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("User authentication: {}", authentication != null ? authentication.getName() : "null");
            logger.info("User authorities: {}", authentication != null ? authentication.getAuthorities() : "null");
            
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getPrincipal().equals("anonymousUser")) {
                logger.warn("User not authenticated, redirecting to login");
                return "redirect:/login";
            }
            
            try {
                User user = userService.getUserByEmail(authentication.getName());
                
                if (user == null) {
                    logger.error("User not found for email: {}", authentication.getName());
                    model.addAttribute("error", "User not found");
                    return "error/500";
                }

                logger.info("User details - ID: {}, Name: {}, Email: {}, Role: {}", 
                    user.getId(), user.getName(), user.getEmail(), user.getRole());

                model.addAttribute("user", user);
                logger.info("User role: {}", user.getRole());

                if (user.getRole() == User.UserRole.CANDIDATE) {
                    logger.info("Handling candidate dashboard for user: {}", user.getEmail());
                    return handleCandidateDashboard(model, user);
                } else if (user.getRole() == User.UserRole.COMPANY) {
                    logger.info("Handling company dashboard for user: {}", user.getEmail());
                    return handleCompanyDashboard(model, user);
                } else {
                    logger.error("Invalid user role: {}", user.getRole());
                    model.addAttribute("error", "Invalid user role");
                    return "error/403";
                }
            } catch (Exception e) {
                logger.error("Error retrieving user data: {}", e.getMessage(), e);
                model.addAttribute("error", "Error retrieving user data: " + e.getMessage());
                return "error/500";
            }
        } catch (Exception e) {
            logger.error("Critical error in dashboard method: {}", e.getMessage(), e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "error/500";
        }
    }

    private String handleCandidateDashboard(Model model, User user) {
        try {
            logger.info("Loading candidate dashboard for user: {}", user.getEmail());
            
            // Add user data
            model.addAttribute("user", user);
            
            // Add dashboard statistics
            model.addAttribute("completedExams", resultService.getCompletedExamsCount(user));
            model.addAttribute("averageScore", resultService.getAverageScore(user));
            model.addAttribute("upcomingExams", examService.getUpcomingExamsCount(user));
            model.addAttribute("upcomingExamsList", examService.getUpcomingExams(user));
            model.addAttribute("recentResults", resultService.getRecentResults(user));
            
            logger.info("Candidate dashboard data loaded successfully");
            return "dashboard/candidate";
        } catch (Exception e) {
            logger.error("Error loading candidate dashboard for user {}: {}", user.getEmail(), e.getMessage(), e);
            model.addAttribute("error", "Error loading dashboard data: " + e.getMessage());
            return "error/500";
        }
    }

    private String handleCompanyDashboard(Model model, User user) {
        try {
            logger.info("Starting to load company dashboard for user: {}", user.getEmail());
            
            if (user == null) {
                logger.error("User object is null");
                model.addAttribute("error", "User not found");
                return "error/500";
            }

            // Log user details for debugging
            logger.info("User details - ID: {}, Name: {}, Email: {}, Role: {}", 
                user.getId(), user.getName(), user.getEmail(), user.getRole());

            if (user.getRole() != User.UserRole.COMPANY) {
                logger.error("User {} does not have company role. Current role: {}", user.getEmail(), user.getRole());
                model.addAttribute("error", "Access denied - Invalid role");
                return "error/403";
            }

            // Add user data
            model.addAttribute("user", user);
            
            // Initialize all required model attributes with default values
            model.addAttribute("totalExams", 0L);
            model.addAttribute("activeExams", 0L);
            model.addAttribute("totalCandidates", 0L);
            model.addAttribute("completedExams", 0L);
            model.addAttribute("activeExamsList", new ArrayList<>());
            model.addAttribute("recentResults", new ArrayList<>());
            
            // Try to load each piece of data independently
            try {
                long totalExams = examService.getTotalExamsCount(user);
                model.addAttribute("totalExams", totalExams);
                logger.info("Total exams loaded: {}", totalExams);
            } catch (Exception e) {
                logger.error("Error loading total exams: {}", e.getMessage(), e);
            }

            try {
                long activeExams = examService.getActiveExamsCount(user);
                model.addAttribute("activeExams", activeExams);
                logger.info("Active exams loaded: {}", activeExams);
            } catch (Exception e) {
                logger.error("Error loading active exams: {}", e.getMessage(), e);
            }

            try {
                long totalCandidates = resultService.getTotalCandidatesCount(user);
                model.addAttribute("totalCandidates", totalCandidates);
                logger.info("Total candidates loaded: {}", totalCandidates);
            } catch (Exception e) {
                logger.error("Error loading total candidates: {}", e.getMessage(), e);
            }

            try {
                long completedExams = resultService.getCompletedExamsCount(user);
                model.addAttribute("completedExams", completedExams);
                logger.info("Completed exams loaded: {}", completedExams);
            } catch (Exception e) {
                logger.error("Error loading completed exams: {}", e.getMessage(), e);
            }

            try {
                List<Exam> activeExamsList = examService.getActiveExams(user);
                model.addAttribute("activeExamsList", activeExamsList != null ? activeExamsList : new ArrayList<>());
                logger.info("Active exams list loaded, size: {}", activeExamsList != null ? activeExamsList.size() : 0);
            } catch (Exception e) {
                logger.error("Error loading active exams list: {}", e.getMessage(), e);
            }

            try {
                List<Result> recentResults = resultService.getRecentResults(user);
                model.addAttribute("recentResults", recentResults != null ? recentResults : new ArrayList<Result>());
                logger.info("Recent results loaded, size: {}", recentResults != null ? recentResults.size() : 0);
            } catch (Exception e) {
                logger.error("Error loading recent results: {}", e.getMessage(), e);
            }

            logger.info("Company dashboard data loaded successfully");
            return "dashboard/company";
            
        } catch (Exception e) {
            logger.error("Critical error in handleCompanyDashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "An unexpected error occurred while loading the dashboard. Please try again later.");
            return "error/500";
        }
    }

    @GetMapping("/api/dashboard/company")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<?> getCompanyDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User company = userService.getUserByEmail(auth.getName());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Company Dashboard");
        response.put("username", auth.getName());
        response.put("roles", auth.getAuthorities());
        response.put("totalCandidates", resultRepository.countDistinctCandidateByExamCompany(company));
        response.put("totalResults", resultRepository.countByExamCompany(company));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/dashboard/candidate")
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE')")
    public ResponseEntity<?> getCandidateDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User candidate = userService.getUserByEmail(auth.getName());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Candidate Dashboard");
        response.put("username", auth.getName());
        response.put("roles", auth.getAuthorities());
        response.put("totalExams", resultRepository.countByCandidate(candidate));
        response.put("averageScore", resultRepository.findAverageScoreByCandidate(candidate));
        response.put("recentResults", resultRepository.findByCandidateOrderBySubmissionTimeDesc(candidate));
        
        return ResponseEntity.ok(response);
    }
} 