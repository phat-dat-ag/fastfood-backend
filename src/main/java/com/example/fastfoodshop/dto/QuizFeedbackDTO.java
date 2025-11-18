package com.example.fastfoodshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class QuizFeedbackDTO {
    private Long quizId;
    private String userName;
    private String topicName;
    private String topicDifficultyName;
    private String feedback;
    private LocalDateTime feedbackAt;
}
