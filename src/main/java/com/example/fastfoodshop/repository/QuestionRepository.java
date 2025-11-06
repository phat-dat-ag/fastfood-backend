package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
