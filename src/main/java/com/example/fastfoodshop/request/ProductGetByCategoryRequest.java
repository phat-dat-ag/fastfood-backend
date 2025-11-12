package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductGetByCategoryRequest extends PageRequest {
    @NotBlank(message = "Không được để trống thông tin danh mục")
    private String categorySlug;
}
