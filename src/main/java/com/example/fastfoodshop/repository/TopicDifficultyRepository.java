package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.projection.TopicDifficultyStatsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicDifficultyRepository extends JpaRepository<TopicDifficulty, Long> {
    boolean existsBySlug(String slug);

    Optional<TopicDifficulty> findBySlugAndIsDeletedFalse(String slug);

    Optional<TopicDifficulty> findByIdAndIsDeletedFalseAndIsActivatedTrue(Long topicDifficultyId);

    Optional<TopicDifficulty> findByIdAndIsDeletedFalseAndIsActivatedFalse(Long topicDifficultyId);

    Page<TopicDifficulty> findByTopicAndIsDeletedFalse(Topic topic, Pageable pageable);

    @Query(value = """
             SELECT
             	td.id,
                 td.name,
                 COUNT(q.id) AS total_quizzes_played,
                 COUNT(q.promotion_id) AS total_promotions_received,
                 COALESCE(
             		AVG(
             			CASE WHEN q.completed_at IS NOT NULL
             				THEN TIMESTAMPDIFF(SECOND, q.started_at, q.completed_at)
             			END
             		), 0
             	) AS avg_duration_seconds
             FROM topic_difficulties td
             LEFT JOIN  quizzes q ON q.topic_difficulty_id = td.id
             WHERE td.is_deleted = false
             GROUP BY td.id
            """, nativeQuery = true)
    List<TopicDifficultyStatsProjection> getStats();

    @Query(value = """
            SELECT td.*
            FROM topic_difficulties td
            JOIN awards a ON td.id = a.topic_difficulty_id
            JOIN questions q ON td.id = q.topic_difficulty_id
            WHERE td.slug = :slug
            AND a.used_quantity < a.quantity
            GROUP BY td.id
            HAVING COUNT(q.id) > td.question_count
            """, nativeQuery = true
    )
    Optional<TopicDifficulty> findPlayableBySlug(@Param("slug") String slug);
}
