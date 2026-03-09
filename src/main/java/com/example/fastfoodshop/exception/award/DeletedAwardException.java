package com.example.fastfoodshop.exception.award;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DeletedAwardException extends BusinessException {
    public DeletedAwardException() {
        super("DELETED_AWARD", "Phần thưởng đã bị xóa");
    }
}
