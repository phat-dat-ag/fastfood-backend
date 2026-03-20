package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressCreateRequest(
        @NotBlank(message = "Tên địa chỉ không được để trống")
        @Size(min = 3, max = 40, message = "Tên địa chỉ từ 3 đến 40 ký tự")
        String name,

        @Size(max = 100, message = "Nội dung ghi chú không quá 100 ký tự")

        String detail,

        @NotNull(message = "Vĩ độ không được để trống")
        Double latitude,

        @NotNull(message = "Kinh độ không được để trống")
        Double longitude,

        String street,

        String ward,

        String district,

        String province
) {
}