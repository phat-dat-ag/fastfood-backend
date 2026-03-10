package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.enums.PromotionType;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record AwardDTO(
        Long id,
        PromotionType type,
        int minValue,
        int maxValue,
        int usedQuantity,
        int quantity,
        int maxDiscountAmount,
        int minSpendAmount,
        boolean activated,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AwardDTO from(Award award) {
        return new AwardDTO(
                award.getId(),
                award.getType(),
                award.getMinValue(),
                award.getMaxValue(),
                award.getUsedQuantity(),
                award.getQuantity(),
                award.getMaxDiscountAmount(),
                award.getMinSpendAmount(),
                award.isActivated(),
                award.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                award.getUpdatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
    }
}
