package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotNull;

public record DeliveryRequest(
        @NotNull(message = "Không được để trống vị trí giao hàng")
        Long addressId
) {
}
