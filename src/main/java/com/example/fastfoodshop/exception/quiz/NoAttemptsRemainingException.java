package com.example.fastfoodshop.exception.quiz;

import com.example.fastfoodshop.exception.base.BusinessException;

public class NoAttemptsRemainingException extends BusinessException {
    public NoAttemptsRemainingException() {
        super("NO_ATTEMPT_REMAINING", "Hết lượt tham gia hôm nay");
    }
}
