package com.example.fastfoodshop.exception.topic_difficulty;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DeletedTopicDifficultyException extends BusinessException {
    public DeletedTopicDifficultyException() {
        super("DELETED_TOPIC_DIFFICULTY", "Độ khó đã bị xóa");
    }
}
