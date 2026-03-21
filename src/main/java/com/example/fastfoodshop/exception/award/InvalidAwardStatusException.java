package com.example.fastfoodshop.exception.award;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidAwardStatusException extends BusinessException {
    public InvalidAwardStatusException(Long awardId) {
        super("INVALID_AWARD_STATUS", "Trạng thái phần thưởng không hợp lệ: " + awardId);
    }
}
