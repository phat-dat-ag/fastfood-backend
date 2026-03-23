package com.example.fastfoodshop.exception.topic;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DeletedTopicException extends BusinessException {
    public DeletedTopicException() {
        super("DELETED_TOPIC", "Chủ đề đã bị xóa");
    }
}
