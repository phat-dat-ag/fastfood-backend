package com.example.fastfoodshop.exception.question;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DeletedQuestionException extends BusinessException {
    public DeletedQuestionException(Long questionId) {
        super("DELETED_QUESTION", "Câu hỏi đã bị xóa: " + questionId);
    }
}
