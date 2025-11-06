package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Answer;
import lombok.Data;

@Data
public class AnswerDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private boolean isCorrect;

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent() != null ? answer.getContent() : "";
        this.imageUrl = answer.getImageUrl();
        this.isCorrect = answer.isCorrect();
    }
}
