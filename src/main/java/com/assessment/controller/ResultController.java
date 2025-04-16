package com.assessment.controller;

import com.assessment.entity.Exam;
import com.assessment.entity.Result;
import com.assessment.entity.User;
import com.assessment.service.ExamService;
import com.assessment.service.ResultService;
import com.assessment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
public class ResultController {
    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExamService examService;

    @PostMapping("/submit/{examId}")
    public ResponseEntity<Result> submitExam(
            @PathVariable Long examId,
            @RequestBody Map<Long, Integer> answers,
            Authentication authentication) {
        User candidate = userService.getUserByEmail(authentication.getName());
        Exam exam = examService.getExamsByCompany(null).stream()
            .filter(e -> e.getId().equals(examId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        return ResponseEntity.ok(resultService.submitExam(exam, candidate, answers));
    }

    @GetMapping("/candidate")
    public ResponseEntity<List<Result>> getCandidateResults(Authentication authentication) {
        User candidate = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(resultService.getResultsByCandidate(candidate));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<Result>> getExamResults(@PathVariable Long examId) {
        Exam exam = examService.getExamsByCompany(null).stream()
            .filter(e -> e.getId().equals(examId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        return ResponseEntity.ok(resultService.getResultsByExam(exam));
    }

    @GetMapping("/exam/{examId}/candidate")
    public ResponseEntity<Result> getCandidateExamResult(
            @PathVariable Long examId,
            Authentication authentication) {
        User candidate = userService.getUserByEmail(authentication.getName());
        Exam exam = examService.getExamsByCompany(null).stream()
            .filter(e -> e.getId().equals(examId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        return ResponseEntity.ok(resultService.getResultByCandidateAndExam(candidate, exam));
    }
} 