package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
