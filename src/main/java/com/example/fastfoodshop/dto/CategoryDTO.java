package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
    private boolean isDeleted;
    private ArrayList<ProductDTO> products = new ArrayList<>();

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
        this.isDeleted = category.isDeleted();
        List<Product> categories = category.getProducts();
        for (Product product : categories) {
            this.products.add(new ProductDTO(product));
        }
    }
}
