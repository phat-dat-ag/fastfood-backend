package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderCancelRequest {
    @NotBlank(message = "Không được bỏ trống lý do hủy đơn")
    private String reason;
}
