package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String categoryImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.slug = category.getSlug();
        this.description = category.getDescription();
        this.categoryImageUrl = category.getCategoryImageUrl();
        this.createdAt = category.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.updatedAt = category.getUpdatedAt()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.isDeleted = category.isDeleted();
    }
}
