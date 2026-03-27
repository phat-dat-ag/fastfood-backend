package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuizAddFeedbackRequest(
        @NotBlank(message = "Không được để trống nội dung góp ý trò chơi")
        @Size(max = 500, message = "Nội dung góp ý không quá 500 ký tự")
        String feedback
) {
}
