package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifySignUpRequest(
        @NotBlank(message = "Số điện thoại không được bỏ trống")
        @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại không hợp lệ")
        String phone,

        @NotBlank(message = "Mã OTP không được bỏ trống")
        String otp
) {
}
