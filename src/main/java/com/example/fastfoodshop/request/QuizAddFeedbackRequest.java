package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuizAddFeedbackRequest {
    @NotNull(message = "Không được để trống thông tin bài làm")
    private Long quizId;

    @NotBlank(message = "Không được để trống nội dung góp ý trò chơi")
    @Size(max = 500, message = "Nội dung góp ý không quá 500 ký tự")
    private String feedback;
}
