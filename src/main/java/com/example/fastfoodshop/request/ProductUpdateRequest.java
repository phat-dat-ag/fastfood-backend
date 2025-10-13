package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductUpdateRequest {
    @NotNull(message = "Không được để trống id sản phẩm")
    private Long id;

    @NotBlank(message = "Tên sản phâ không được để trống")
    @Size(min = 2, max = 80, message = "Tên sản phẩm từ 2 đến 80 kí tự")
    private String name;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    @Size(max = 100, message = "Mô tả danh mục tối đa 100 kí tự")
    private String description;

    @NotNull(message = "Không được để trống trạng thái sản phẩm")
    private boolean isActivated;

    private MultipartFile imageUrl;
}
