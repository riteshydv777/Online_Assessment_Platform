package com.assesment.company.service;

import com.assesment.company.entity.Question;
import com.assesment.company.entity.User;
import com.assesment.company.repository.QuestionRepository;
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

        existingQuestion.setText(updatedQuestion.getText());
        existingQuestion.setType(updatedQuestion.getType());
        existingQuestion.setPoints(updatedQuestion.getPoints());
        existingQuestion.getOptions().clear();
        existingQuestion.getOptions().addAll(updatedQuestion.getOptions());

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