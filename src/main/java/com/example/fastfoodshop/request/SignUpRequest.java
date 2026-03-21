package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Tên không được để trống")
        @Size(min = 2, max = 50, message = "Tên phải từ 2 đến 50 ký tự")
        String name,

        @NotBlank(message = "Số điện thoại không được bỏ trống")
        @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại không hợp lệ")
        String phone,

        @NotBlank(message = "Email không được bỏ trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu không được bỏ trống")
        @Size(min = 8, max = 20, message = "Mật khẩu phải từ 8 đến 20 ký tự")
        String password,

        @NotBlank(message = "Ngày sinh không được để trống")
        String birthdayString
) {
}
