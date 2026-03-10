package com.example.fastfoodshop.exception.topic;

import com.example.fastfoodshop.exception.base.BusinessException;

public class TopicNotFoundException extends BusinessException {
    public TopicNotFoundException(Long topicId) {
        super("TOPIC_NOT_FOUND", "Chủ đề không tồn tại hoặc b xóa: " + topicId);
    }

    public TopicNotFoundException(String topicSlug) {
        super("TOPIC_NOT_FOUND", "Chủ đề không tồn tại hoặc b xóa: " + topicSlug);
    }
}
