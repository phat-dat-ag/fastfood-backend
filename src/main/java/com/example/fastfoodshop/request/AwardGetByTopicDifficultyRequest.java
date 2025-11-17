package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwardGetByTopicDifficultyRequest extends PageRequest {
    @NotBlank(message = "Thông tin độ khó không được để trống")
    private String topicDifficultySlug;
}
