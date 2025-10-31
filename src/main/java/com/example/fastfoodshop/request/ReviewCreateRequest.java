package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReviewCreateRequest {
    @NotNull(message = "Không được để trống mã sản phẩm")
    private Long productId;

    @NotNull(message = "Không được để trống điểm đánh giá")
    @Min(value = 1, message = "Điểm đánh giá nhỏ nhất là 1")
    @Max(value = 5, message = "Điểm đánh giá lớn nhất là 5")
    private int rating;

    @NotBlank(message = "Không được để trống bình luận")
    private String comment;

    private List<MultipartFile> images;
}
