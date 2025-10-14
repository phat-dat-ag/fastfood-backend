package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUpdateRequest {
    @NotNull(message = "Không được để trống số lượng sản phẩm")
    @Min(value = 1, message = "Số lượng sản phẩm phải từ 1 trở lên")
    private int quantity;

    @NotNull(message = "Không được để trống mã sản phẩm")
    private Long productId;
}
