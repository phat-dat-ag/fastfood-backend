package com.example.fastfoodshop.exception.topic_difficulty;

import com.example.fastfoodshop.exception.base.BusinessException;

public class TopicDifficultyNotFoundException extends BusinessException {
    public TopicDifficultyNotFoundException(Long topicDifficultyId) {
        super("TOPIC_DIFFICULTY_NOT_FOUND", "Độ khó không tồn tại: " + topicDifficultyId);
    }

    public TopicDifficultyNotFoundException(String topicDifficultySlug) {
        super("TOPIC_DIFFICULTY_NOT_FOUND", "Độ khó không tồn tại: " + topicDifficultySlug);
    }
}
