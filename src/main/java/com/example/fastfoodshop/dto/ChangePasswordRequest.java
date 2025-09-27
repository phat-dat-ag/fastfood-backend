package com.example.fastfoodshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Mật khẩu không được bỏ trống")
    private String password;

    @NotBlank(message = "Mật khẩu mới không được bỏ trống")
    private String newPassword;
}
