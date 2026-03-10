package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Answer;

public record AnswerDTO(
        Long id,
        String content,
        String imageUrl,
        boolean correct,
        boolean selectedByUser
) {
    private static String safeContent(String content) {
        return content != null ? content : "";
    }

    public static AnswerDTO createAdminAnswer(Answer answer) {
        return new AnswerDTO(
                answer.getId(),
                safeContent(answer.getContent()),
                answer.getImageUrl(),
                answer.isCorrect(),
                false
        );
    }

    public static AnswerDTO createUserAnswer(Answer answer) {
        return new AnswerDTO(
                answer.getId(),
                safeContent(answer.getContent()),
                answer.getImageUrl(),
                false,
                false
        );
    }

    public static AnswerDTO createReviewAnswer(Answer answer, boolean isSelectedByUser) {
        return new AnswerDTO(
                answer.getId(),
                safeContent(answer.getContent()),
                answer.getImageUrl(),
                answer.isCorrect(),
                isSelectedByUser
        );
    }
}
