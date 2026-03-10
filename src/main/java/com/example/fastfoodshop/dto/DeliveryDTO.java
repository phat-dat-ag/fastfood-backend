package com.example.fastfoodshop.dto;

public record DeliveryDTO(
        boolean success,
        String message,
        Double distanceKm,
        int durationMinutes,
        int fee
) {
    public static DeliveryDTO accept(Double distanceKm, int durationMinutes, int fee) {
        return new DeliveryDTO(
                true,
                "Đã xác định địa điểm giao hàng",
                distanceKm,
                durationMinutes,
                fee
        );
    }

    public static DeliveryDTO reject(String message) {
        return new DeliveryDTO(
                false,
                message,
                null,
                0,
                0
        );
    }
}
