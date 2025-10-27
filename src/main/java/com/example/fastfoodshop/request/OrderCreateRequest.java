package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateRequest {
    private String userNote;

    @NotNull(message = "Không được để trống địa chỉ giao hàng")
    private Long addressId;
}
