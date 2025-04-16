package com.assessment.repository;

import com.assessment.entity.Question;
import com.assessment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCompanyAndIsActiveTrue(User company);
    List<Question> findByCompany(User company);
} 