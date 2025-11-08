package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByUserAndStartedAtBetween(User user, LocalDateTime start, LocalDateTime end);
}
