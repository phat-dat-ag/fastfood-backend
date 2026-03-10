package com.example.fastfoodshop.exception.quiz;

import com.example.fastfoodshop.exception.base.BusinessException;

public class NotEnoughQuestionsException extends BusinessException {
    public NotEnoughQuestionsException() {
        super("NOT_ENOUGH_QUESTION", "Không đủ câu hỏi để tạo trò chơi");
    }
}
