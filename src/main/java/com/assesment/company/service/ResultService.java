package com.assesment.company.service;

import com.assesment.company.entity.*;
import com.assesment.company.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResultService {
    private static final Logger logger = LoggerFactory.getLogger(ResultService.class);

    @Autowired
    private ResultRepository resultRepository;

    public Result submitExam(Exam exam, User candidate, Map<Long, Integer> answers) {
        try {
            if (resultRepository.findByCandidateAndExam(candidate, exam).isPresent()) {
                throw new RuntimeException("You have already submitted this exam");
            }

            int totalQuestions = exam.getQuestions().size();
            int correctAnswers = 0;

            for (Question question : exam.getQuestions()) {
                Integer selectedOptionIndex = answers.get(question.getId());
                if (selectedOptionIndex != null && selectedOptionIndex >= 0 && selectedOptionIndex < question.getOptions().size()) {
                    if (question.getOptions().get(selectedOptionIndex).isCorrect()) {
                        correctAnswers++;
                    }
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
        } catch (Exception e) {
            logger.error("Error submitting exam for candidate {}: {}", candidate.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to submit exam", e);
        }
    }

    public List<Result> getResultsByCandidate(User candidate) {
        try {
            return resultRepository.findByCandidate(candidate);
        } catch (Exception e) {
            logger.error("Error getting results for candidate {}: {}", candidate.getEmail(), e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Result> getResultsByExam(Exam exam) {
        try {
            return resultRepository.findByExam(exam);
        } catch (Exception e) {
            logger.error("Error getting results for exam {}: {}", exam.getId(), e.getMessage());
            return new ArrayList<>();
        }
    }

    public Result getResultByCandidateAndExam(User candidate, Exam exam) {
        try {
            return resultRepository.findByCandidateAndExam(candidate, exam)
                .orElseThrow(() -> new RuntimeException("Result not found"));
        } catch (Exception e) {
            logger.error("Error getting result for candidate {} and exam {}: {}", 
                candidate.getEmail(), exam.getId(), e.getMessage());
            throw new RuntimeException("Failed to get result", e);
        }
    }

    public long getCompletedExamsCount(User user) {
        try {
            if (user.getRole().toString().equals("ROLE_CANDIDATE")) {
                return resultRepository.countByCandidate(user);
            } else {
                return resultRepository.countByExamCompany(user);
            }
        } catch (Exception e) {
            logger.error("Error getting completed exams count for user {}: {}", user.getEmail(), e.getMessage());
            return 0;
        }
    }

    public double getAverageScore(User candidate) {
        try {
            return resultRepository.findAverageScoreByCandidate(candidate);
        } catch (Exception e) {
            logger.error("Error getting average score for candidate {}: {}", candidate.getEmail(), e.getMessage());
            return 0.0;
        }
    }

    public long getTotalCandidatesCount(User company) {
        try {
            return resultRepository.countDistinctCandidateByExamCompany(company);
        } catch (Exception e) {
            logger.error("Error getting total candidates count for company {}: {}", company.getEmail(), e.getMessage());
            return 0;
        }
    }

    public List<Result> getRecentResults(User user) {
        try {
            if (user.getRole().toString().equals("ROLE_CANDIDATE")) {
                return resultRepository.findByCandidateOrderBySubmissionTimeDesc(user);
            } else {
                return resultRepository.findByExamCompanyOrderBySubmissionTimeDesc(user);
            }
        } catch (Exception e) {
            logger.error("Error getting recent results for user {}: {}", user.getEmail(), e.getMessage());
            return new ArrayList<>();
        }
    }
} 