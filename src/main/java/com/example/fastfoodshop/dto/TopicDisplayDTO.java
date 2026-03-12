package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.response.DifficultyDisplayResponse;

import java.util.List;

public record TopicDisplayDTO(
        Long id,
        String name,
        String slug,
        String description,
        List<DifficultyDisplayResponse> difficulties
) {
    public static TopicDisplayDTO from(
            TopicDifficultyFullDTO first,
            List<DifficultyDisplayResponse> difficulties
    ) {
        return new TopicDisplayDTO(
                first.topicId(),
                first.topicName(),
                first.topicSlug(),
                first.topicDescription(),
                difficulties
        );
    }
}
