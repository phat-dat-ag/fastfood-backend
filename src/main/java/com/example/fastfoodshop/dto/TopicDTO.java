package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Topic;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record TopicDTO(
        Long id,
        String name,
        String slug,
        String description,
        boolean isActivated,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TopicDTO from(Topic topic) {
        return new TopicDTO(
                topic.getId(),
                topic.getName(),
                topic.getSlug(),
                topic.getDescription(),
                topic.isActivated(),
                topic.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                topic.getUpdatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
    }
}
