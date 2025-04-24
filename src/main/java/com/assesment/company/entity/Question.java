package com.assesment.company.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Column(nullable = false)
    private int points = 1;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    private List<QuestionOption> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private User company;

    @ManyToMany(mappedBy = "questions")
    private List<Exam> exams = new ArrayList<>();

    @Column(nullable = false)
    private boolean isActive = true;

    @Data
    @Embeddable
    public static class QuestionOption {
        @Column(nullable = false)
        private String text;
        
        @Column(nullable = false)
        private boolean correct;
    }

    public enum QuestionType {
        MULTIPLE_CHOICE,
        TRUE_FALSE,
        SHORT_ANSWER
    }
}
