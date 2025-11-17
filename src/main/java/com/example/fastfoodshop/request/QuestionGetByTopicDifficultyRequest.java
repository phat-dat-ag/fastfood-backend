package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionGetByTopicDifficultyRequest extends PageRequest {
    @NotBlank(message = "Không được để trống thông tin độ khó")
    private String topicDifficultySlug;
}
