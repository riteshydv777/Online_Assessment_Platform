package com.assessment.repository;

import com.assessment.entity.Exam;
import com.assessment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCompany(User company);
    List<Exam> findByIsActiveTrueAndStartTimeBeforeAndEndTimeAfter(
        LocalDateTime currentTime, LocalDateTime currentTime2);
} 