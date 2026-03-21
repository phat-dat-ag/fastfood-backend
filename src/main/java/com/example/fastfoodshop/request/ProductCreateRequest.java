package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record ProductCreateRequest(
        @NotNull(message = "ID danh mục không được để trống")
        Long categoryId,

        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(min = 2, max = 80, message = "Tên sản phẩm từ 2 đến 80 kí tự")
        String name,

        @NotBlank(message = "Mô tả sản phẩm không được để trống")
        @Size(max = 100, message = "Mô tả sản phẩm tối đa 100 kí tự")
        String description,

        @NotNull(message = "Giá sản phẩm không được để trống")
        @Min(value = 0, message = "Giá sản phẩm không được nhỏ hơn 0")
        int price,

        @NotNull(message = "Trang thái sản phẩm không được để trống")
        boolean activated,

        MultipartFile imageUrl,
        MultipartFile modelUrl
) {
}
