package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Answer;
import lombok.Data;

@Data
public class AnswerUserDTO {
    private Long id;
    private String content;
    private String imageUrl;

    public AnswerUserDTO(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent() != null ? answer.getContent() : "";
        this.imageUrl = answer.getImageUrl() != null ? answer.getImageUrl() : "";
    }
}
