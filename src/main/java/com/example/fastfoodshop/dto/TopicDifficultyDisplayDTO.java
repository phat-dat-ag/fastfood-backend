package com.example.fastfoodshop.dto;

public record TopicDifficultyDisplayDTO(
        Long id,
        String name,
        String slug,
        String description,
        int duration,
        int questionCount,
        int minCorrectToReward
) {
}
