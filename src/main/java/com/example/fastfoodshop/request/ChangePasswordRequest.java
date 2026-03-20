package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Mật khẩu không được bỏ trống")
        String password,

        @NotBlank(message = "Mật khẩu mới không được bỏ trống")
        String newPassword
) {
}
