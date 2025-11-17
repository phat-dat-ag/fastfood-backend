package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AwardRepository extends JpaRepository<Award, Long> {
    Page<Award> findByTopicDifficultyAndIsDeletedFalse(TopicDifficulty topicDifficulty, Pageable pageable);

    Optional<Award> findByIdAndIsDeletedFalseAndIsActivatedTrue(Long awardId);

    Optional<Award> findByIdAndIsDeletedFalseAndIsActivatedFalse(Long awardId);

    @Query(value = """
                SELECT *
                FROM awards
                WHERE topic_difficulty_id = :topicDifficultyId
                AND is_deleted = false
                AND is_activated = true
                AND used_quantity < quantity
            """, nativeQuery = true)
    List<Award> findAvailableByTopicDifficulty(@Param("topicDifficultyId") Long topicDifficultyId);

    @Query(value = """
                SELECT *
                FROM awards
                WHERE topic_difficulty_id = :topicDifficultyId
            """, nativeQuery = true)
    Optional<Award> findAnyAvailableAwardAsFallback(@Param("topicDifficultyId") Long topicDifficultyId);
}
