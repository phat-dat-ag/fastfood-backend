package com.example.fastfoodshop.response.topic_difficulty;

import com.example.fastfoodshop.dto.TopicDifficultyStatsDTO;

import java.util.List;

public record TopicDifficultyStatsResponse(
        List<TopicDifficultyStatsDTO> topicDifficultyStats
) {
}
