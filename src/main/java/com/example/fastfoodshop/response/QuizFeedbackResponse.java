package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.QuizFeedbackDTO;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.TopicDifficulty;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuizFeedbackResponse {
    private List<QuizFeedbackDTO> feedbacks = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public QuizFeedbackResponse(Page<Quiz> page) {
        for (Quiz quiz : page.getContent()) {
            String userName = quiz.getUser().getName();
            TopicDifficulty topicDifficulty = quiz.getTopicDifficulty();
            String topicName = topicDifficulty.getTopic().getName();
            String topicDifficultyName = topicDifficulty.getName();
            this.feedbacks.add(new QuizFeedbackDTO(quiz.getId(), userName, topicName, topicDifficultyName, quiz.getFeedback(), quiz.getFeedbackAt()));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
