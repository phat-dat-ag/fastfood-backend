package com.example.fastfoodshop.dto;

public record PromotionCodeCheckResultDTO(
        boolean success,
        String message,
        PromotionDTO promotion
) {
    public static PromotionCodeCheckResultDTO success(String message, PromotionDTO promotionDTO) {
        return new PromotionCodeCheckResultDTO(true, message, promotionDTO);
    }

    public static PromotionCodeCheckResultDTO error(String message) {
        return new PromotionCodeCheckResultDTO(false, message, null);
    }
}
