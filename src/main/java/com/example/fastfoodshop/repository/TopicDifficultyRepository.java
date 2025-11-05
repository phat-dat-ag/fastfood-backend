package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicDifficultyRepository extends JpaRepository<TopicDifficulty, Long> {
    boolean existsBySlug(String slug);
}
