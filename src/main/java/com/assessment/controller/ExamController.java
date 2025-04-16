package com.assessment.controller;

import com.assessment.entity.Exam;
import com.assessment.entity.User;
import com.assessment.service.ExamService;
import com.assessment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    @Autowired
    private ExamService examService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam, Authentication authentication) {
        User company = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(examService.createExam(exam, company));
    }

    @GetMapping("/company")
    public ResponseEntity<List<Exam>> getCompanyExams(Authentication authentication) {
        User company = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(examService.getExamsByCompany(company));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Exam>> getActiveExams() {
        return ResponseEntity.ok(examService.getActiveExams());
    }

    @PostMapping("/{examId}/questions")
    public ResponseEntity<Exam> addQuestionsToExam(
            @PathVariable Long examId,
            @RequestBody List<Long> questionIds,
            Authentication authentication) {
        User company = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(examService.addQuestionsToExam(examId, questionIds, company));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id, Authentication authentication) {
        User company = userService.getUserByEmail(authentication.getName());
        examService.deleteExam(id, company);
        return ResponseEntity.ok().build();
    }
} 