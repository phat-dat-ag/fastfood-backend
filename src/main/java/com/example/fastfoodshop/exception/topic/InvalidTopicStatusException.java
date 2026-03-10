package com.example.fastfoodshop.exception.topic;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidTopicStatusException extends BusinessException {
    public InvalidTopicStatusException(Long topicId) {
        super("INVALID_TOPIC_STATS", "Không tìm thấy chủ đề có trạng thái hợp lệ: " + topicId);
    }
}
