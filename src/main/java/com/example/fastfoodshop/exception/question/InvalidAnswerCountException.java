package com.example.fastfoodshop.exception.question;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidAnswerCountException extends BusinessException {
    public InvalidAnswerCountException(int answerCount) {
        super("INVALID_ANSWER_COUNT", "Số lượng đáp án không đủ để tạo câu hỏi: " + answerCount);
    }
}
