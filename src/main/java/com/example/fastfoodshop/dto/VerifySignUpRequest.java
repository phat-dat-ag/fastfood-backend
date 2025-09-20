package com.example.fastfoodshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifySignUpRequest {
    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Mã OTP không được bỏ trống")
    private String otp;
}
