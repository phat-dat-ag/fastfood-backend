package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotNull;

public record QuizQuestionSubmitRequest(
        @NotNull(message = "Mã câu hỏi không được để trống")
        Long questionId,

        Long answerId
) {
}
