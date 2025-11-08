package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicDifficultyRepository extends JpaRepository<TopicDifficulty, Long> {
    boolean existsBySlug(String slug);

    Optional<TopicDifficulty> findBySlugAndIsDeletedFalse(String slug);

    List<TopicDifficulty> findByTopicAndIsDeletedFalse(Topic topic);


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
