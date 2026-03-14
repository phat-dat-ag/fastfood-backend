package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.enums.SectionType;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record ImageDTO(
        Long id,
        SectionType sectionType,
        String url,
        String alternativeText,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ImageDTO from(Image image) {
        return new ImageDTO(
                image.getId(),
                image.getSectionType(),
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
