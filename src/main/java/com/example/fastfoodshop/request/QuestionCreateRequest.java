package com.example.fastfoodshop.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record QuestionCreateRequest(
        @NotBlank(message = "Nội dung câu hỏi không được để trống")
        @Size(max = 2000, message = "Nội dung câu hỏi không quá 2000 ký tự")
        String content,

        MultipartFile imageUrl,

        MultipartFile audioUrl,

        @NotNull(message = "Trạng thái của câu hỏi không được để trống")
        Boolean activated,

        @Valid
        List<AnswerCreateRequest> answers
) {
}
