package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicCreateRequest {
    @NotBlank(message = "Tên chủ đề không được để trống")
    @Size(min = 2, max = 60, message = "Tên chủ đề từ 2 đến 60 ký tự")
    private String name;

    @NotBlank(message = "Mô tả chủ đề không được để trống")
    @Size(min = 2, max = 2000, message = "Mô tả chủ đề từ 2 đến 2000 ký tự")
    private String description;

    @NotNull(message = "Không được để trống trạng thái của chủ đề")
    private Boolean isActivated;
}
