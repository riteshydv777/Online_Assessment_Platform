package com.assesment.company.repository;

import com.assesment.company.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
