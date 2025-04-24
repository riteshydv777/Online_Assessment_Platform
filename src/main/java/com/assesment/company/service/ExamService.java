package com.assesment.company.service;

import com.assesment.company.entity.Exam;
import com.assesment.company.entity.Question;
import com.assesment.company.entity.User;
import com.assesment.company.repository.ExamRepository;
import com.assesment.company.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExamService {
    private static final Logger logger = LoggerFactory.getLogger(ExamService.class);

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Exam createExam(Exam exam, User company) {
        try {
            exam.setCompany(company);
            return examRepository.save(exam);
        } catch (Exception e) {
            logger.error("Error creating exam: {}", e.getMessage());
            throw new RuntimeException("Failed to create exam", e);
        }
    }

    public List<Exam> getExamsByCompany(User company) {
        try {
            return examRepository.findByCompany(company);
        } catch (Exception e) {
            logger.error("Error getting exams for company {}: {}", company.getEmail(), e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Exam> getActiveExams() {
        try {
            LocalDateTime now = LocalDateTime.now();
            return examRepository.findByIsActiveTrueAndStartTimeBeforeAndEndTimeAfter(now, now);
        } catch (Exception e) {
            logger.error("Error getting active exams: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public Exam addQuestionsToExam(Long examId, List<Long> questionIds, User company) {
        try {
            Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

            if (!exam.getCompany().equals(company)) {
                throw new RuntimeException("Unauthorized to modify this exam");
            }

            List<Question> questions = questionRepository.findAllById(questionIds);
            exam.getQuestions().addAll(questions);
            return examRepository.save(exam);
        } catch (Exception e) {
            logger.error("Error adding questions to exam {}: {}", examId, e.getMessage());
            throw new RuntimeException("Failed to add questions to exam", e);
        }
    }

    public void deleteExam(Long id, User company) {
        try {
            Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

            if (!exam.getCompany().equals(company)) {
                throw new RuntimeException("Unauthorized to delete this exam");
            }

            exam.setActive(false);
            examRepository.save(exam);
        } catch (Exception e) {
            logger.error("Error deleting exam {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete exam", e);
        }
    }

    public long getTotalExamsCount(User company) {
        try {
            return examRepository.countByCompany(company);
        } catch (Exception e) {
            logger.error("Error getting total exams count for company {}: {}", company.getEmail(), e.getMessage());
            return 0;
        }
    }

    public long getActiveExamsCount(User company) {
        try {
            return examRepository.countByCompanyAndIsActiveTrue(company);
        } catch (Exception e) {
            logger.error("Error getting active exams count for company {}: {}", company.getEmail(), e.getMessage());
            return 0;
        }
    }

    public List<Exam> getActiveExams(User company) {
        try {
            return examRepository.findByCompanyAndIsActiveTrue(company);
        } catch (Exception e) {
            logger.error("Error getting active exams for company {}: {}", company.getEmail(), e.getMessage());
            return new ArrayList<>();
        }
    }

    public long getUpcomingExamsCount(User candidate) {
        try {
            LocalDateTime now = LocalDateTime.now();
            return examRepository.countByIsActiveTrueAndStartTimeAfter(now);
        } catch (Exception e) {
            logger.error("Error getting upcoming exams count for candidate {}: {}", candidate.getEmail(), e.getMessage());
            return 0;
        }
    }

    public List<Exam> getUpcomingExams(User candidate) {
        try {
            LocalDateTime now = LocalDateTime.now();
            return examRepository.findByIsActiveTrueAndStartTimeAfterOrderByStartTimeAsc(now);
        } catch (Exception e) {
            logger.error("Error getting upcoming exams for candidate {}: {}", candidate.getEmail(), e.getMessage());
            return new ArrayList<>();
        }
    }

    public void deactivateExam(Long examId) {
        try {
            examRepository.findById(examId).ifPresent(exam -> {
                exam.setActive(false);
                examRepository.save(exam);
            });
        } catch (Exception e) {
            logger.error("Error deactivating exam {}: {}", examId, e.getMessage());
            throw new RuntimeException("Failed to deactivate exam", e);
        }
    }
}
