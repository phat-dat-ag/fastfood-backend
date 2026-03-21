package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;

public record OrderCancelRequest(
        @NotBlank(message = "Không được bỏ trống lý do hủy đơn")
        String reason
) {
}
