package com.example.fastfoodshop.exception.question;

import com.example.fastfoodshop.exception.base.BusinessException;

public class QuestionNotFoundException extends BusinessException {
    public QuestionNotFoundException(Long questionId) {
        super("QUESTION_NOT_FOUND", "Câu hỏi không tồn tại: " + questionId);
    }
}
