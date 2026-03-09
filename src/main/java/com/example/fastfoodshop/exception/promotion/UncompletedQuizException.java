package com.example.fastfoodshop.exception.promotion;

import com.example.fastfoodshop.exception.base.BusinessException;

public class UncompletedQuizException extends BusinessException {
    public UncompletedQuizException() {
        super("UNCOMPLETED_QUIZ", "Lỗi bài kiểm tra chưa hoàn thành");
    }
}
