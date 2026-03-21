package com.example.fastfoodshop.request;

import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.enums.SectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ImageCreateRequest(
        @NotNull(message = "Không được để trống ảnh")
        MultipartFile imageFile,

        @NotBlank(message = "Mô tả ảnh không được để trống")
        String alternativeText,

        @NotNull(message = "Không được để trống loại trang chứa ảnh")
        PageType pageType,

        SectionType sectionType
) {
}
