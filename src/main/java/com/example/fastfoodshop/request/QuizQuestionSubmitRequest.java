package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizQuestionSubmitRequest {
    @NotNull(message = "Mã câu hỏi không được để trống")
    private Long questionId;

    private Long answerId;
}
