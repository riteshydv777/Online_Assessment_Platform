package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.service.ExamService;
import com.assesment.company.service.ResultService;
import com.assesment.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard-api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DashboardRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardRestController.class);

    @Autowired
    private ExamService examService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Processing dashboard stats request for user: {}", auth.getName());
            
            if (auth == null || !auth.isAuthenticated()) {
                logger.error("No authentication found");
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            User user = userService.getUserByEmail(auth.getName());
            if (user == null) {
                logger.error("User not found for email: {}", auth.getName());
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
            ));
            
            try {
                if (user.getRole() == User.UserRole.COMPANY) {
                    response.put("totalExams", examService.getTotalExamsCount(user));
                    response.put("activeExams", examService.getActiveExamsCount(user));
                    response.put("totalCandidates", resultService.getTotalCandidatesCount(user));
                    response.put("completedExams", resultService.getCompletedExamsCount(user));
                    response.put("activeExamsList", examService.getActiveExams(user));
                    response.put("recentResults", resultService.getRecentResults(user));
                } else if (user.getRole() == User.UserRole.CANDIDATE) {
                    response.put("totalExams", resultService.getCompletedExamsCount(user));
                    response.put("averageScore", resultService.getAverageScore(user));
                    response.put("recentResults", resultService.getRecentResults(user));
                }
                
                logger.info("Successfully retrieved dashboard stats for user: {}", user.getEmail());
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Error retrieving dashboard stats: {}", e.getMessage(), e);
                return ResponseEntity.status(500).body(Map.of("error", "Error retrieving dashboard stats"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error in getDashboardStats: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
} 