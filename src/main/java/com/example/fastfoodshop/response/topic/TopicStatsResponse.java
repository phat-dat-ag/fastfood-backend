package com.example.fastfoodshop.response.topic;

import com.example.fastfoodshop.dto.TopicStatsDTO;

import java.util.List;

public record TopicStatsResponse(
        List<TopicStatsDTO> topicStats
) {
}
