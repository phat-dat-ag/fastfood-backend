package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliveryRequest {
    @NotNull(message = "Vĩ độ của khách hàng không được để trống")
    private Double customerLatitude;

    @NotNull(message = "Kinh độ của khách hàng không được để trống")
    private Double customerLongitude;
}
