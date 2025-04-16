package com.assessment.service;

import com.assessment.entity.Question;
import com.assessment.entity.User;
import com.assessment.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public Question createQuestion(Question question, User company) {
        question.setCompany(company);
        return questionRepository.save(question);
    }

    public List<Question> getQuestionsByCompany(User company) {
        return questionRepository.findByCompanyAndIsActiveTrue(company);
    }

    public Question updateQuestion(Long id, Question updatedQuestion, User company) {
        Question existingQuestion = questionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!existingQuestion.getCompany().equals(company)) {
            throw new RuntimeException("Unauthorized to update this question");
        }

        existingQuestion.setQuestionText(updatedQuestion.getQuestionText());
        existingQuestion.setOption1(updatedQuestion.getOption1());
        existingQuestion.setOption2(updatedQuestion.getOption2());
        existingQuestion.setOption3(updatedQuestion.getOption3());
        existingQuestion.setOption4(updatedQuestion.getOption4());
        existingQuestion.setCorrectOption(updatedQuestion.getCorrectOption());

        return questionRepository.save(existingQuestion);
    }

    public void deleteQuestion(Long id, User company) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getCompany().equals(company)) {
            throw new RuntimeException("Unauthorized to delete this question");
        }

        question.setActive(false);
        questionRepository.save(question);
    }
} 