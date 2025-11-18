package com.example.fastfoodshop.projection;

public interface TopicDifficultyStatsProjection {
    Long getId();

    String getName();

    Long getTotalQuizzesPlayed();

    Long getTotalPromotionsReceived();

    Double getAvgDurationSeconds();
}
