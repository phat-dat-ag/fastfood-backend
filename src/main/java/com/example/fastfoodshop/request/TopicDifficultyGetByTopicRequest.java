package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicDifficultyGetByTopicRequest extends PageRequest {
    @NotBlank(message = "Không được để trống thông tin chủ đề")
    private String topicSlug;
}
