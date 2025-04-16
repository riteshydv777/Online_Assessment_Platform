package com.assesment.company.repository;

import com.assesment.company.entity.Result;
import com.assesment.company.entity.User;
import com.assesment.company.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByCandidate(User candidate);
    List<Result> findByExam(Exam exam);
    Optional<Result> findByCandidateAndExam(User candidate, Exam exam);
    
    long countByCandidate(User candidate);
    
    long countByExamCompany(User company);
    
    @Query("SELECT COALESCE(AVG(r.score), 0.0) FROM Result r WHERE r.candidate = ?1")
    Double findAverageScoreByCandidate(User candidate);
    
    @Query("SELECT COUNT(DISTINCT r.candidate) FROM Result r WHERE r.exam.company = ?1")
    long countDistinctCandidateByExamCompany(User company);
    
    List<Result> findByCandidateOrderBySubmissionTimeDesc(User candidate);
    
    List<Result> findByExamCompanyOrderBySubmissionTimeDesc(User company);
} 