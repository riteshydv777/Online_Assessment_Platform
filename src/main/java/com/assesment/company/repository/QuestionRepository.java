package com.assesment.company.repository;

import com.assesment.company.entity.Question;
import com.assesment.company.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCompanyAndIsActiveTrue(User company);
    List<Question> findByCompany(User company);
}
