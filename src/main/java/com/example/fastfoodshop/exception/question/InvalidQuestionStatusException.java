package com.example.fastfoodshop.exception.question;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidQuestionStatusException extends BusinessException {
    public InvalidQuestionStatusException(Long questionId) {
        super("INVALID_QUESTION_STATUS", "Không tìm thấy câu hỏi có trạng thái hợp lệ: " + questionId);
    }
}
