package com.example.fastfoodshop.exception.quiz;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidQuizException extends BusinessException {
    public InvalidQuizException(Long quizId) {
        super("INVALID_QUIZ", "Bài kiểm tra không hợp lệ: " + quizId);
    }
}
