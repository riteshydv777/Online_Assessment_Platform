package com.assessment.repository;

import com.assessment.entity.Result;
import com.assessment.entity.User;
import com.assessment.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByCandidate(User candidate);
    List<Result> findByExam(Exam exam);
    Optional<Result> findByCandidateAndExam(User candidate, Exam exam);
} 