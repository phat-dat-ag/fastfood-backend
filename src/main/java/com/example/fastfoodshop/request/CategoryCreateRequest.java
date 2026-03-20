package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CategoryCreateRequest(
        @NotBlank(message = "Tên danh mục không được để trống")
        @Size(min = 2, max = 80, message = "Tên danh mục từ 2 đến 80 kí tự")
        String name,

        @NotBlank(message = "Mô tả danh mục không được để trống")
        @Size(max = 100, message = "Mô tả danh mục tối đa 100 kí tự")
        String description,

        @NotNull(message = "Trang thái danh mục không được để trống")
        boolean activated,

        MultipartFile imageUrl
) {
}
