package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Answer;
import lombok.Data;

@Data
public class AnswerDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private boolean isCorrect;
    private boolean isSelectedByUser;

    private AnswerDTO(Long id, String content, String imageUrl, boolean isCorrect, boolean isSelectedByUser) {
        this.id = id;
        this.content = content != null ? content : "";
        this.imageUrl = imageUrl;
        this.isCorrect = isCorrect;
        this.isSelectedByUser = isSelectedByUser;
    }

    public static AnswerDTO createAdminAnswer(Answer answer) {
        return new AnswerDTO(answer.getId(), answer.getContent(), answer.getImageUrl(), answer.isCorrect(), false);
    }

    public static AnswerDTO createUserAnswer(Answer answer) {
        return new AnswerDTO(answer.getId(), answer.getContent(), answer.getImageUrl(), false, false);
    }

    public static AnswerDTO createReviewAnswer(Answer answer, boolean isSelectedByUser) {
        return new AnswerDTO(answer.getId(), answer.getContent(), answer.getImageUrl(), answer.isCorrect(), isSelectedByUser);
    }
}
