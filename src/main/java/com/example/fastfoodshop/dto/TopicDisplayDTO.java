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
            TopicDifficultyFullDTO topicDifficultyFullDTO,
            List<TopicDifficultyDisplayDTO> difficulties
    ) {
        return new TopicDisplayDTO(
                topicDifficultyFullDTO.topicId(),
                topicDifficultyFullDTO.topicName(),
                topicDifficultyFullDTO.topicSlug(),
                topicDifficultyFullDTO.topicDescription(),
                difficulties
        );
    }
}
