package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ReviewCreateRequest(
        @NotNull(message = "Không được để trống mã sản phẩm")
        Long productId,

        @NotNull(message = "Không được để trống điểm đánh giá")
        @Min(value = 1, message = "Điểm đánh giá nhỏ nhất là 1")
        @Max(value = 5, message = "Điểm đánh giá lớn nhất là 5")
        int rating,

        @NotBlank(message = "Không được để trống bình luận")
        String comment,

        List<MultipartFile> images
) {
}
