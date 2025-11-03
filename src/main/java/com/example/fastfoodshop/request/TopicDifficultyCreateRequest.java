package com.example.fastfoodshop.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TopicDifficultyCreateRequest {
    @NotBlank(message = "Tên độ khó không được để trống")
    @Size(min = 2, max = 60, message = "Tên độ khó từ 2 đến 60 ký tự")
    private String name;

    @NotBlank(message = "Mô tả độ khó không được để trống")
    @Size(min = 2, max = 2000, message = "Mô tả độ khó từ 2 đến 2000 ký tự")
    private String description;

    @NotNull(message = "Thời lượng làm bài không được để trống")
    @Min(value = 30, message = "Thời lượng làm bài phải nhiều hơn 30 giây")
    @Max(value = 900, message = "Thời lượng làm bài phải dưới 15 phút")
    private Integer duration;

    @NotNull(message = "Số lượng câu hỏi không được để trống")
    @Min(value = 1, message = "Số lượng câu hỏi phải lớn hơn 1")
    private Integer questionCount;

    @NotNull(message = "Số lượng câu hỏi cần trả lời đúng không được để trống")
    @Min(value = 1, message = "Số lượng câu hỏi cần trả lời đúng phải lớn hơn 1")
    private Integer minCorrectToReward;

    @NotNull(message = "Trạng thái của của chủ đề không được để trống")
    private Boolean isActivated;

    @AssertTrue(message = "Số lượng câu hỏi cần trả lời đúng phải nhỏ hơn số lượng câu hỏi tối đa")
    public boolean isQuestionCountValid() {
        return questionCount == null || minCorrectToReward == null || minCorrectToReward < questionCount;
    }
}
