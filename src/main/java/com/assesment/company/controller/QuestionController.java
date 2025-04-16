package com.assesment.company.controller;

import com.assesment.company.entity.Question;
import com.assesment.company.entity.User;
import com.assesment.company.service.QuestionService;
import com.assesment.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question question, Authentication authentication) {
        User company = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(questionService.createQuestion(question, company));
    }

    @GetMapping("/company")
    public ResponseEntity<List<Question>> getCompanyQuestions(Authentication authentication) {
        User company = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(questionService.getQuestionsByCompany(company));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id,
            @RequestBody Question question,
            Authentication authentication) {
        User company = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(questionService.updateQuestion(id, question, company));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id, Authentication authentication) {
        User company = userService.getUserByEmail(authentication.getName());
        questionService.deleteQuestion(id, company);
        return ResponseEntity.ok().build();
    }
} 