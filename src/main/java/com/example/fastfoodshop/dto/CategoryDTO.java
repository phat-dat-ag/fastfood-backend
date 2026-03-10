package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Category;

import java.time.LocalDateTime;
import java.time.ZoneId;;

public record CategoryDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isActivated
) {
    public static CategoryDTO from(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl(),
                category.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                category.getUpdatedAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                category.isActivated()
        );
    }
}
