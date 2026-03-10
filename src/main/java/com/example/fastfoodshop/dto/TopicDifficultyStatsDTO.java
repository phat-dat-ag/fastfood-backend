package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.projection.TopicDifficultyStatsProjection;

public record TopicDifficultyStatsDTO(
        Long id,
        String name,
        Long totalQuizzesPlayed,
        Long totalPromotionsReceived,
        Double avgDurationSeconds
) {
    public static TopicDifficultyStatsDTO from(TopicDifficultyStatsProjection statsProjection) {
        return new TopicDifficultyStatsDTO(
                statsProjection.getId(),
                statsProjection.getName(),
                statsProjection.getTotalQuizzesPlayed(),
                statsProjection.getTotalPromotionsReceived(),
                statsProjection.getAvgDurationSeconds()
        );
    }
}
