package com.example.fastfoodshop.exception.auth;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidUserStatusException extends BusinessException {
    public InvalidUserStatusException() {
        super("INVALID_USER_STATUS", "Trạng thái tài khoản không hợp lệ");
    }
}
