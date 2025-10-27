package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliveryRequest {
    @NotNull(message = "Không được để trống vị trí giao hàng")
    private Long addressId;
}
