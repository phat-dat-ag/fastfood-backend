package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.enums.PromotionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class PromotionDTO {
    private Long id;
    private PromotionType type;
    private int value;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int quantity;
    private int usedQuantity;
    private int maxDiscountAmount;
    private int minSpendAmount;
    private String code;
    private boolean isGlobal;
    private boolean isActivated;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CategoryDTO category;
    private ProductDTO product;
    private UserDTO user;

    public PromotionDTO(Promotion promotion) {
        this.id = promotion.getId();
        this.type = promotion.getType();
        this.value = promotion.getValue();
        this.startAt = promotion.getStartAt();
        this.endAt = promotion.getEndAt();
        this.quantity = promotion.getQuantity();
        this.usedQuantity = promotion.getUsedQuantity();
        this.maxDiscountAmount = promotion.getMaxDiscountAmount();
        ;
        this.minSpendAmount = promotion.getMinSpendAmount();
        this.code = promotion.getCode();
        this.isGlobal = promotion.isGlobal();
        this.isActivated = promotion.isActivated();
        this.isDeleted = promotion.isDeleted();
        this.createdAt = promotion.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.updatedAt = promotion.getUpdatedAt()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.category = promotion.getCategory() != null ? new CategoryDTO(promotion.getCategory()) : null;
        this.product = promotion.getProduct() != null ? new ProductDTO(promotion.getProduct()) : null;
        this.user = promotion.getUser() != null ? new UserDTO(promotion.getUser()) : null;
    }
}
