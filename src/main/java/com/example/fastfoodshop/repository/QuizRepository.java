package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByUserAndStartedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    List<Quiz> findByUserAndCompletedAtIsNotNull(User user);

    Optional<Quiz> findByIdAndUserAndCompletedAtIsNotNull(Long quizId, User user);

    Optional<Quiz> findByIdAndUserAndTopicDifficultyAndCompletedAtIsNull(Long quizId, User user, TopicDifficulty topicDifficulty);

    Page<Quiz> findByFeedbackAtIsNotNull(Pageable pageable);
}
