package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.projection.ItemPromotionProjection;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ItemPromotionDTO {
    private String name;
    private String url;
    private String type;
    private BigDecimal value;
    private String code;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public ItemPromotionDTO(ItemPromotionProjection p) {
        this.name = p.getName();
        this.url = p.getImageUrl();
        this.type = p.getType();
        this.value = p.getValue();
        this.code = p.getCode();
        this.startAt = p.getStartAt();
        this.endAt = p.getEndAt();
    }
}
