package com.example.fastfoodshop.dto;

import lombok.Data;

@Data
public class DeliveryDTO {
    private boolean success;
    private String message;
    private Double distanceKm;
    private int durationMinutes;
    private int fee;

    public DeliveryDTO(boolean success, String message, Double distanceKm, int durationMinutes, int fee) {
        this.success = success;
        this.message = message;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
        this.fee = fee;
    }

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
