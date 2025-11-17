package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.dto.TopicDifficultyFullDTO;
import com.example.fastfoodshop.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsBySlug(String slug);

    Optional<Topic> findBySlugAndIsDeletedFalse(String slug);

    Optional<Topic> findByIdAndIsDeletedFalseAndIsActivatedTrue(Long topicId);

    Optional<Topic> findByIdAndIsDeletedFalseAndIsActivatedFalse(Long topicId);

    Page<Topic> findByIsDeletedFalse(Pageable pageable);

    @Query("""
                SELECT new com.example.fastfoodshop.dto.TopicDifficultyFullDTO(
                    t.id,
                    t.name,
                    t.slug,
                    t.description,
            
                    d.id,
                    d.name,
                    d.slug,
                    d.description,
                    d.duration,
                    d.questionCount,
                    d.minCorrectToReward
                )
                FROM Topic t
                JOIN t.topicDifficulties d
                WHERE
                    t.isActivated = true
                    AND t.isDeleted = false
                    AND d.isActivated = true
                    AND d.isDeleted = false
                    AND EXISTS (
                        SELECT 1 FROM Award a
                        WHERE a.topicDifficulty = d
                          AND a.isActivated = true
                          AND a.isDeleted = false
                          AND a.usedQuantity < a.quantity
                    )
                    AND (
                        SELECT COUNT(q)
                        FROM Question q
                        WHERE q.topicDifficulty = d
                          AND q.isActivated = true
                          AND q.isDeleted = false
                    ) >= d.questionCount
            
                ORDER BY t.name ASC, d.name ASC
            """)
    List<TopicDifficultyFullDTO> findDisplayableTopicsFull();
}
