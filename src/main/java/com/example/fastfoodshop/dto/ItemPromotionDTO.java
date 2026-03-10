package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.projection.ItemPromotionProjection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemPromotionDTO(
        String name,
        String url,
        String type,
        BigDecimal value,
        String code,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static ItemPromotionDTO from(ItemPromotionProjection itemPromotionProjection) {
        return new ItemPromotionDTO(
                itemPromotionProjection.getName(),
                itemPromotionProjection.getImageUrl(),
                itemPromotionProjection.getType(),
                itemPromotionProjection.getValue(),
                itemPromotionProjection.getCode(),
                itemPromotionProjection.getStartAt(),
                itemPromotionProjection.getEndAt()
        );
    }
}
