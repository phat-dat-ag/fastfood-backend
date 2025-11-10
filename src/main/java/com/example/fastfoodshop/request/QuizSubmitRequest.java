package com.example.fastfoodshop.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmitRequest {
    @NotNull(message = "Không được để trống mã bài kiểm tra")
    private Long quizId;

    @NotBlank(message = "Không được để trống độ khó của bài kiểm tra")
    private String topicDifficultySlug;

    @Valid
    @NotEmpty(message = "Danh sách các phản hồi không được rỗng")
    private List<QuizQuestionSubmitRequest> quizQuestions;
}
