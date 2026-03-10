package com.example.fastfoodshop.exception.topic_difficulty;

import com.example.fastfoodshop.exception.base.BusinessException;

public class UnavailableTopicDifficultyException extends BusinessException {
    public UnavailableTopicDifficultyException() {
        super("UNAVAILABLE_TOPIC_DIFFICULTY", "Chủ đề thiếu câu hỏi hoặc phần thưởng");
    }
}
