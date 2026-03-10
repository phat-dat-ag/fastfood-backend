package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Image;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record ImageDTO(
        Long id,
        String url,
        String alternativeText,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ImageDTO from(Image image) {
        return new ImageDTO(
                image.getId(),
                image.getUrl(),
                image.getAlternativeText(),
                image.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                image.getUpdatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
    }
}
