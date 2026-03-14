package com.example.fastfoodshop.dto;

import java.util.List;

public record TopicDisplayDTO(
        Long id,
        String name,
        String slug,
        String description,
        List<TopicDifficultyDisplayDTO> difficulties
) {
    public static TopicDisplayDTO from(
            TopicDifficultyFullDTO first,
            List<TopicDifficultyDisplayDTO> difficulties
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
