package com.assesment.company.repository;

import com.assesment.company.entity.Exam;
import com.assesment.company.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    long countByCompany(User company);
    
    long countByCompanyAndIsActiveTrue(User company);
    
    List<Exam> findByCompanyAndIsActiveTrue(User company);
    
    long countByIsActiveTrueAndStartTimeAfter(LocalDateTime dateTime);
    
    List<Exam> findByIsActiveTrueAndStartTimeAfterOrderByStartTimeAsc(LocalDateTime dateTime);
    
    List<Exam> findByCompany(User company);
    
    List<Exam> findByIsActiveTrueAndStartTimeBeforeAndEndTimeAfter(
        LocalDateTime currentTime, LocalDateTime currentTime2);
}
