package com.example.fastfoodshop.exception.award;

import com.example.fastfoodshop.exception.base.BusinessException;

public class AwardNotFoundException extends BusinessException {
    public AwardNotFoundException(Long awardId) {
        super("AWARD_NOT_FOUND", "Phần thưởng không tồn tại: " + awardId);
    }

    public AwardNotFoundException() {
        super("AWARD_NOT_FOUND", "Phần thưởng không tồn tại");
    }
}
