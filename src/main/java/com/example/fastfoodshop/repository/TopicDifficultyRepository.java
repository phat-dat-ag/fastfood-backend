package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicDifficultyRepository extends JpaRepository<TopicDifficulty, Long> {
    boolean existsBySlug(String slug);

    List<TopicDifficulty> findByTopicAndIsDeletedFalse(Topic topic);
}
