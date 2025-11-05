package com.example.fastfoodshop.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TopicDifficultyUpdateRequest {
    @NotBlank(message = "Tên độ khó không được để trống")
    @Size(min = 2, max = 60, message = "Tên độ khó từ 2 đến 60 ký tự")
    private String name;

    @NotBlank(message = "Mô tả độ khó không được để trống")
    @Size(min = 2, max = 2000, message = "Mô tả độ khó từ 2 đến 2000 ký tự")
    private String description;

    @NotNull(message = "Trạng thái của của chủ đề không được để trống")
    private Boolean isActivated;
}
