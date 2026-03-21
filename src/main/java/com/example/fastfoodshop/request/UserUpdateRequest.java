package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "Tên không được để trống")
        @Size(min = 2, max = 50, message = "Tên phải từ 2 đến 50 ký tự")
        String name,

        @NotBlank(message = "Email không được bỏ trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        String email,

        @NotBlank(message = "Ngày sinh không được để trống")
        String birthdayString
) {
}
