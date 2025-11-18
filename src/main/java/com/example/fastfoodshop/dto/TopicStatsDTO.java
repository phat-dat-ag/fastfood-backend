package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.projection.TopicStatsProjection;
import lombok.Data;

@Data
public class TopicStatsDTO {
    private Long id;
    private String name;
    private Long totalQuizzesPlayed;
    private Long totalPromotionsReceived;

    public TopicStatsDTO(TopicStatsProjection statsProjection) {
        this.id = statsProjection.getId();
        this.name = statsProjection.getName();
        this.totalQuizzesPlayed = statsProjection.getTotalQuizzesPlayed();
        this.totalPromotionsReceived = statsProjection.getTotalPromotionsReceived();
    }
}
