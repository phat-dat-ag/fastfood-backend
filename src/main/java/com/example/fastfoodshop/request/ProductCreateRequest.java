package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductCreateRequest {
    @NotNull(message = "ID danh mujc không được để trống")
    private Long category_id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 15, message = "Tên sản phẩm từ 2 đến 15 kí tự")
    private String name;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    @Size(max = 100, message = "Mô tả sản phẩm tối đa 100 kí tự")
    private String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm không được nhỏ hơn 0")
    private int price;

    @NotNull(message = "Trang thái sản phẩm không được để trống")
    private boolean activated;

    private MultipartFile imageUrl;
}
