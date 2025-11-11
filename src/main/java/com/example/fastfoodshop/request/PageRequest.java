package com.example.fastfoodshop.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageRequest {
    @Min(value = 0, message = "Trang phải lớn hơn hoặc bằng 0")
    private int page = 0;

    @Min(value = 1, message = "Kích thước trang ít nhất là 1")
    @Max(value = 20, message = "kích thước trang tối đa là 20")
    private int size = 10;
}
