package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TopicDifficultyUpdateRequest(
        @NotBlank(message = "Tên độ khó không được để trống")
        @Size(min = 2, max = 60, message = "Tên độ khó từ 2 đến 60 ký tự")
        String name,

        @NotBlank(message = "Mô tả độ khó không được để trống")
        @Size(min = 2, max = 2000, message = "Mô tả độ khó từ 2 đến 2000 ký tự")
        String description,

        @NotNull(message = "Trạng thái của của chủ đề không được để trống")
        Boolean activated
) {
}
