package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.projection.TopicDifficultyStatsProjection;
import lombok.Data;

@Data
public class TopicDifficultyStatsDTO {
    private Long id;
    private String name;
    private Long totalQuizzesPlayed;
    private Long totalPromotionsReceived;
    private Double avgDurationSeconds;

    public TopicDifficultyStatsDTO(TopicDifficultyStatsProjection statsProjection) {
        this.id = statsProjection.getId();
        this.name = statsProjection.getName();
        this.totalQuizzesPlayed = statsProjection.getTotalQuizzesPlayed();
        this.totalPromotionsReceived = statsProjection.getTotalPromotionsReceived();
        this.avgDurationSeconds = statsProjection.getAvgDurationSeconds();
    }
}
