package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class ProductDTO {
    private Long categoryId;
    private String categoryName;
    private Long id;
    private String name;
    private String slug;
    private int price;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActivated;
    private boolean isDeleted;

    public ProductDTO(Product product) {
        this.categoryId = product.getCategory().getId();
        this.categoryName = product.getCategory().getName();
        this.id = product.getId();
        this.name = product.getName();
        this.slug = product.getSlug();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.createdAt = product.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
        this.updatedAt = product.getUpdatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
        this.isActivated = product.isActivated();
        this.isDeleted = product.isDeleted();
    }
}
