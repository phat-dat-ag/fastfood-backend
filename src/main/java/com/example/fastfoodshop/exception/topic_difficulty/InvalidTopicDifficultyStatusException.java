package com.example.fastfoodshop.exception.topic_difficulty;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidTopicDifficultyStatusException extends BusinessException {
    public InvalidTopicDifficultyStatusException() {
        super("INVALID_TOPIC_DIFFICULTY", "Không có độ khó chủ đề hợp lệ");
    }
}
