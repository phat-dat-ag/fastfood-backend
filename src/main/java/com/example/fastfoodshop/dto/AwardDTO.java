package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.enums.PromotionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class AwardDTO {
    private Long id;
    private PromotionType type;
    private int minValue;
    private int maxValue;
    private int usedQuantity;
    private int quantity;
    private int maxDiscountAmount;
    private int minSpendAmount;
    private boolean isActivated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AwardDTO(Award award) {
        this.id = award.getId();
        this.type = award.getType();
        this.minValue = award.getMinValue();
        this.maxValue = award.getMaxValue();
        this.usedQuantity = award.getUsedQuantity();
        this.quantity = award.getQuantity();
        this.maxDiscountAmount = award.getMaxDiscountAmount();
        this.minSpendAmount = award.getMinSpendAmount();
        this.isActivated = award.isActivated();
        this.createdAt = award.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
        this.updatedAt = award.getUpdatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
    }
}
