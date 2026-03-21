package com.example.fastfoodshop.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuizSubmitRequest(
        @NotNull(message = "Không được để trống mã bài kiểm tra")
        Long quizId,

        @NotBlank(message = "Không được để trống độ khó của bài kiểm tra")
        String topicDifficultySlug,

        @Valid
        @NotEmpty(message = "Danh sách các phản hồi không được rỗng")
        List<QuizQuestionSubmitRequest> quizQuestions
) {
}
