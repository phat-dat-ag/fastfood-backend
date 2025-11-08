package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
}
