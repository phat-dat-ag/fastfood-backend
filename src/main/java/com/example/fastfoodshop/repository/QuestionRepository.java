package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTopicDifficultyAndIsDeletedFalse(TopicDifficulty topicDifficulty);

    List<Question> findByTopicDifficultyAndIsActivatedTrueAndIsDeletedFalse(TopicDifficulty topicDifficulty);
}
