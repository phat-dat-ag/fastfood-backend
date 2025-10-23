package com.example.fastfoodshop.dto;

import lombok.Data;

@Data
public class PromotionCodeCheckResultDTO {
    private boolean success;
    private String message;
    private PromotionDTO promotion;

    public PromotionCodeCheckResultDTO(boolean success, String message, PromotionDTO promotionDTO) {
        this.success = success;
        this.message = message;
        this.promotion = promotionDTO;
    }

    public static PromotionCodeCheckResultDTO success(String message, PromotionDTO promotionDTO) {
        return new PromotionCodeCheckResultDTO(true, message, promotionDTO);
    }

    public static PromotionCodeCheckResultDTO error(String message) {
        return new PromotionCodeCheckResultDTO(false, message, null);
    }
}
