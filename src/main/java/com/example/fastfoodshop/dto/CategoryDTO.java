package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActivated;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.slug = category.getSlug();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.createdAt = category.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.updatedAt = category.getUpdatedAt()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.isActivated = category.isActivated();
    }
}
