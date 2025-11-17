package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndIsDeletedFalse(Long questionId);

    Optional<Question> findByIdAndIsDeletedFalseAndIsActivatedTrue(Long questionId);

    Optional<Question> findByIdAndIsDeletedFalseAndIsActivatedFalse(Long questionId);

    Page<Question> findByTopicDifficultyAndIsDeletedFalse(TopicDifficulty topicDifficulty, Pageable pageable);

    List<Question> findByTopicDifficultyAndIsActivatedTrueAndIsDeletedFalse(TopicDifficulty topicDifficulty);
}
