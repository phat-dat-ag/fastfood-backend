package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ForgetPasswordRequest(
        @NotBlank(message = "Số điện thoại không được bỏ trống")
        @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại không hợp lệ")
        String phone
) {
}
