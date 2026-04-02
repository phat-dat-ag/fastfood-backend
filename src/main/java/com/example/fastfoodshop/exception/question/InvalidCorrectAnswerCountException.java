package com.example.fastfoodshop.exception.question;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidCorrectAnswerCountException extends BusinessException {
    public InvalidCorrectAnswerCountException(long correctAnswerCount) {
        super("INVALID_CORRECT_ANSWER_COUNT", "Số lượng đáp án đúng không hợp lệ" + correctAnswerCount);
    }
}
