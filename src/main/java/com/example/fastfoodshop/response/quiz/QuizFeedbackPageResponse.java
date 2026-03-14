package com.example.fastfoodshop.response.quiz;

import com.example.fastfoodshop.dto.QuizFeedbackDTO;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record QuizFeedbackPageResponse(
        List<QuizFeedbackDTO> feedbacks,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static QuizFeedbackPageResponse from(Page<Quiz> page) {
        List<QuizFeedbackDTO> feedbacks = new ArrayList<>();
        for (Quiz quiz : page.getContent()) {
            String userName = quiz.getUser().getName();
            TopicDifficulty topicDifficulty = quiz.getTopicDifficulty();
            String topicName = topicDifficulty.getTopic().getName();
            String topicDifficultyName = topicDifficulty.getName();
            feedbacks.add(
                    new QuizFeedbackDTO(
                            quiz.getId(), userName,
                            topicName, topicDifficultyName, quiz.getFeedback(), quiz.getFeedbackAt()
                    )
            );
        }

        return new QuizFeedbackPageResponse(
                feedbacks,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
