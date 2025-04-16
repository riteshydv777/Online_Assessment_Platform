package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.service.ExamService;
import com.assesment.company.service.ResultService;
import com.assesment.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DashboardRestController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("user", user);

            if (user.getRole() == User.UserRole.CANDIDATE) {
                return ResponseEntity.ok(getCandidateDashboardData(user));
            } else {
                return ResponseEntity.ok(getCompanyDashboardData(user));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to load dashboard",
                "message", e.getMessage()
            ));
        }
    }

    private Map<String, Object> getCandidateDashboardData(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("completedExams", resultService.getCompletedExamsCount(user));
        data.put("averageScore", resultService.getAverageScore(user));
        data.put("upcomingExams", examService.getUpcomingExamsCount(user));
        data.put("upcomingExamsList", examService.getUpcomingExams(user));
        data.put("recentResults", resultService.getRecentResults(user));
        return data;
    }

    private Map<String, Object> getCompanyDashboardData(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("totalExams", examService.getTotalExamsCount(user));
        data.put("activeExams", examService.getActiveExamsCount(user));
        data.put("totalCandidates", resultService.getTotalCandidatesCount(user));
        data.put("completedExams", resultService.getCompletedExamsCount(user));
        data.put("activeExamsList", examService.getActiveExams(user));
        data.put("recentResults", resultService.getRecentResults(user));
        return data;
    }
} 