package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
        String userNote,

        String promotionCode,

        @NotNull(message = "Không được để trống địa chỉ giao hàng")
        Long addressId
) {
}
