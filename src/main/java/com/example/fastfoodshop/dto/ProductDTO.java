package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.Review;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;

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
    private String modelUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActivated;
    private boolean isDeleted;
    private ArrayList<PromotionDTO> promotions = new ArrayList<>();
    private ArrayList<ReviewDTO> reviews = new ArrayList<>();
    private int discountedPrice;
    private Long promotionId;

    public ProductDTO(Product product) {
        this.categoryId = product.getCategory().getId();
        this.categoryName = product.getCategory().getName();
        this.id = product.getId();
        this.name = product.getName();
        this.slug = product.getSlug();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.modelUrl = product.getModelUrl();
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
        this.discountedPrice = product.getPrice();
        this.promotionId = null;

        for (Promotion promotion : product.getPromotions()) {
            this.promotions.add(new PromotionDTO(promotion));
        }
        for (Review review : product.getReviews().stream()
                .sorted(Comparator.comparing(Review::getRating).reversed())
                .limit(5)
                .toList()) {
            this.reviews.add(new ReviewDTO(review));
        }
    }
}
