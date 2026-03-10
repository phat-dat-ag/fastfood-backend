package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.TopicDifficulty;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record TopicDifficultyDTO(
        String topicName,
        Long id,
        String name,
        String slug,
        String description,
        int duration,
        int questionCount,
        int minCorrectToReward,
        boolean activated,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TopicDifficultyDTO from(TopicDifficulty topicDifficulty) {
        return new TopicDifficultyDTO(
                topicDifficulty.getTopic().getName(),
                topicDifficulty.getId(),
                topicDifficulty.getName(),
                topicDifficulty.getSlug(),
                topicDifficulty.getDescription(),
                topicDifficulty.getDuration(),
                topicDifficulty.getQuestionCount(),
                topicDifficulty.getMinCorrectToReward(),
                topicDifficulty.isActivated(),
                topicDifficulty.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                topicDifficulty.getUpdatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
    }
}
