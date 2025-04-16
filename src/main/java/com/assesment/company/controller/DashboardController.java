package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.service.ExamService;
import com.assesment.company.service.ResultService;
import com.assesment.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@Controller
public class DashboardController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResultRepository resultRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);

        if (user.getRole() == User.UserRole.CANDIDATE) {
            return handleCandidateDashboard(user, model);
        } else {
            return handleCompanyDashboard(user, model);
        }
    }

    private String handleCandidateDashboard(User user, Model model) {
        model.addAttribute("completedExams", resultService.getCompletedExamsCount(user));
        model.addAttribute("averageScore", resultService.getAverageScore(user));
        model.addAttribute("upcomingExams", examService.getUpcomingExamsCount(user));
        model.addAttribute("upcomingExamsList", examService.getUpcomingExams(user));
        model.addAttribute("recentResults", resultService.getRecentResults(user));
        
        return "dashboard/candidate";
    }

    private String handleCompanyDashboard(User user, Model model) {
        model.addAttribute("totalExams", examService.getTotalExamsCount(user));
        model.addAttribute("activeExams", examService.getActiveExamsCount(user));
        model.addAttribute("totalCandidates", resultService.getTotalCandidatesCount(user));
        model.addAttribute("completedExams", resultService.getCompletedExamsCount(user));
        model.addAttribute("activeExamsList", examService.getActiveExams(user));
        model.addAttribute("recentResults", resultService.getRecentResults(user));
        
        return "dashboard/company";
    }

    @GetMapping("/api/dashboard/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> getCompanyDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User company = userService.getCurrentUser();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Company Dashboard");
        response.put("username", auth.getName());
        response.put("roles", auth.getAuthorities());
        response.put("totalCandidates", resultRepository.countDistinctCandidateByExamCompany(company));
        response.put("totalResults", resultRepository.countByExamCompany(company));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/dashboard/candidate")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> getCandidateDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User candidate = userService.getCurrentUser();
        
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