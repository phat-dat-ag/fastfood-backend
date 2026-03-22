package com.example.fastfoodshop.request;

import com.example.fastfoodshop.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
        @NotNull(message = "Không được để trống phương thức thanh toán")
        PaymentMethod paymentMethod,

        String userNote,

        String promotionCode,

        @NotNull(message = "Không được để trống địa chỉ giao hàng")
        Long addressId
) {
}
