package com.assesment.company.service;

import com.assesment.company.entity.Exam;
import com.assesment.company.entity.Question;
import com.assesment.company.entity.User;
import com.assesment.company.repository.ExamRepository;
import com.assesment.company.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamService {
    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Exam createExam(Exam exam, User company) {
        exam.setCompany(company);
        return examRepository.save(exam);
    }

    public List<Exam> getExamsByCompany(User company) {
        return examRepository.findByCompany(company);
    }

    public List<Exam> getActiveExams() {
        LocalDateTime now = LocalDateTime.now();
        return examRepository.findByIsActiveTrueAndStartTimeBeforeAndEndTimeAfter(now, now);
    }

    public Exam addQuestionsToExam(Long examId, List<Long> questionIds, User company) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (!exam.getCompany().equals(company)) {
            throw new RuntimeException("Unauthorized to modify this exam");
        }

        List<Question> questions = questionRepository.findAllById(questionIds);
        exam.getQuestions().addAll(questions);
        return examRepository.save(exam);
    }

    public void deleteExam(Long id, User company) {
        Exam exam = examRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (!exam.getCompany().equals(company)) {
            throw new RuntimeException("Unauthorized to delete this exam");
        }

        exam.setActive(false);
        examRepository.save(exam);
    }

    public long getTotalExamsCount(User company) {
        return examRepository.countByCompany(company);
    }

    public long getActiveExamsCount(User company) {
        return examRepository.countByCompanyAndIsActiveTrue(company);
    }

    public List<Exam> getActiveExams(User company) {
        return examRepository.findByCompanyAndIsActiveTrue(company);
    }

    public long getUpcomingExamsCount(User candidate) {
        LocalDateTime now = LocalDateTime.now();
        return examRepository.countByIsActiveTrueAndStartTimeAfter(now);
    }

    public List<Exam> getUpcomingExams(User candidate) {
        LocalDateTime now = LocalDateTime.now();
        return examRepository.findByIsActiveTrueAndStartTimeAfterOrderByStartTimeAsc(now);
    }

    public void deactivateExam(Long examId) {
        examRepository.findById(examId).ifPresent(exam -> {
            exam.setActive(false);
            examRepository.save(exam);
        });
    }
}
