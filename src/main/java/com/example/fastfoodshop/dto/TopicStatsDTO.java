package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.projection.TopicStatsProjection;

public record TopicStatsDTO(
        Long id,
        String name,
        Long totalQuizzesPlayed,
        Long totalPromotionsReceived
) {
    public static TopicStatsDTO from(TopicStatsProjection statsProjection) {
        return new TopicStatsDTO(
                statsProjection.getId(),
                statsProjection.getName(),
                statsProjection.getTotalQuizzesPlayed(),
                statsProjection.getTotalPromotionsReceived()
        );
    }
}
