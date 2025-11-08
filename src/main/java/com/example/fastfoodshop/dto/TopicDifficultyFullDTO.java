package com.example.fastfoodshop.dto;

public record TopicDifficultyFullDTO(
        Long topicId,
        String topicName,
        String topicSlug,
        String topicDescription,

        Long difficultyId,
        String difficultyName,
        String difficultySlug,
        String difficultyDescription,
        int duration,
        int questionCount,
        int minCorrectToReward
) {
}