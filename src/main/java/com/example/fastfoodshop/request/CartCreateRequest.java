package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartCreateRequest(
        @NotNull(message = "Không được để trống số lượng sản phẩm")
        @Min(value = 1, message = "Số lượng sản phẩm phải từ 1 trở lên")
        int quantity,

        @NotNull(message = "Không được để trống mã sản phẩm")
        Long productId
) {
}
