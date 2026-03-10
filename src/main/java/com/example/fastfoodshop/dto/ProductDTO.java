package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Product;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public record ProductDTO(
        Long categoryId,
        String categoryName,
        Long id,
        String name,
        String slug,
        int price,
        String description,
        String imageUrl,
        String modelUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean activated,
        boolean deleted,
        List<PromotionDTO> promotions,
        List<ReviewDTO> reviews,
        int discountedPrice,
        Long promotionId,

        double averageRating,
        long reviewCount,
        long soldCount
) {
    private static ProductDTO create(
            Product product, List<ReviewDTO> reviews,
            int discountedPrice, Long promotionId, double averageRating, long reviewCount, long soldCount
    ) {
        List<PromotionDTO> promotions = product.getPromotions()
                .stream().map(PromotionDTO::from).toList();

        return new ProductDTO(
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getPrice(),
                product.getDescription(),
                product.getImageUrl(),
                product.getModelUrl(),
                product.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                product.getUpdatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                product.isActivated(),
                product.isDeleted(),
                promotions,
                reviews,
                discountedPrice,
                promotionId,
                averageRating,
                reviewCount,
                soldCount
        );
    }

    public static ProductDTO from(Product product) {
        return create(
                product, List.of(), product.getPrice(), null,
                0.0, 0, 0
        );
    }

    public static ProductDTO from(
            Product product, List<ReviewDTO> reviews,
            PromotionResult promotionResult, double averageRating, long reviewCount, long soldCount
    ) {
        return create(
                product, reviews, promotionResult.discountedPrice(), promotionResult.promotionId(),
                averageRating, reviewCount, soldCount
        );
    }
}
