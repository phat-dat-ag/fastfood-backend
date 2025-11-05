package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Long> {
    List<Award> findByTopicDifficultyAndIsDeletedFalse(TopicDifficulty topicDifficulty);
}
