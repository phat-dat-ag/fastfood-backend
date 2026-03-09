package com.example.fastfoodshop.exception.auth;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException() {
        super("INVALID_PASSWORD", "Mật khẩu không hợp lệ");
    }
}
