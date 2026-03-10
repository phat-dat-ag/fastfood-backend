package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.enums.PromotionType;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record PromotionDTO(
        Long id,
        PromotionType type,
        int value,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int quantity,
        int usedQuantity,
        int maxDiscountAmount,
        int minSpendAmount,
        String code,
        boolean global,
        boolean activated,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String name
) {
    public static PromotionDTO from(Promotion promotion) {
        String name = "";
        if (promotion.getCategory() != null)
            name = promotion.getCategory().getName();
        else if (promotion.getProduct() != null)
            name = promotion.getProduct().getName();
        else if (promotion.getUser() != null)
            name = promotion.getUser().getName();

        return new PromotionDTO(
                promotion.getId(),
                promotion.getType(),
                promotion.getValue(),
                promotion.getStartAt(),
                promotion.getEndAt(),
                promotion.getQuantity(),
                promotion.getUsedQuantity(),
                promotion.getMaxDiscountAmount(),
                promotion.getMinSpendAmount(),
                promotion.getCode(),
                promotion.isGlobal(),
                promotion.isActivated(),
                promotion.isDeleted(),
                promotion.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                promotion.getUpdatedAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                name
        );
    }
}
