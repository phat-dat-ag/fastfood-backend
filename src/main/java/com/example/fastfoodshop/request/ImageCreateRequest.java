package com.example.fastfoodshop.request;

import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.enums.SectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageCreateRequest {
    @NotNull(message = "Không được để trống ảnh")
    private MultipartFile imageFile;

    @NotBlank(message = "Mô tả ảnh không được để trống")
    private String alternativeText;

    @NotNull(message = "Không được để trống loại trang chứa ảnh")
    private PageType pageType;

    private SectionType sectionType;
}
