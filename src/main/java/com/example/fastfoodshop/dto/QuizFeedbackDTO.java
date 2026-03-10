package com.example.fastfoodshop.dto;

import java.time.LocalDateTime;

public record QuizFeedbackDTO(
        Long quizId,
        String userName,
        String topicName,
        String topicDifficultyName,
        String feedback,
        LocalDateTime feedbackAt
) {
}
