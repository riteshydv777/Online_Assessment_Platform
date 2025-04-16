package com.assessment.service;

import com.assessment.entity.*;
import com.assessment.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    public Result submitExam(Exam exam, User candidate, Map<Long, Integer> answers) {
        if (resultRepository.findByCandidateAndExam(candidate, exam).isPresent()) {
            throw new RuntimeException("You have already submitted this exam");
        }

        int totalQuestions = exam.getQuestions().size();
        int correctAnswers = 0;

        for (Question question : exam.getQuestions()) {
            Integer selectedOption = answers.get(question.getId());
            if (selectedOption != null && selectedOption == question.getCorrectOption()) {
                correctAnswers++;
            }
        }

        double score = ((double) correctAnswers / totalQuestions) * 100;
        boolean isPassed = score >= 60; // Assuming passing score is 60%

        Result result = new Result();
        result.setCandidate(candidate);
        result.setExam(exam);
        result.setTotalQuestions(totalQuestions);
        result.setCorrectAnswers(correctAnswers);
        result.setScore(score);
        result.setSubmissionTime(LocalDateTime.now());
        result.setPassed(isPassed);

        return resultRepository.save(result);
    }

    public List<Result> getResultsByCandidate(User candidate) {
        return resultRepository.findByCandidate(candidate);
    }

    public List<Result> getResultsByExam(Exam exam) {
        return resultRepository.findByExam(exam);
    }

    public Result getResultByCandidateAndExam(User candidate, Exam exam) {
        return resultRepository.findByCandidateAndExam(candidate, exam)
            .orElseThrow(() -> new RuntimeException("Result not found"));
    }
} 