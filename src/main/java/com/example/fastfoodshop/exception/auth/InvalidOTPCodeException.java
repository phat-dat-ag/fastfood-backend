package com.example.fastfoodshop.exception.auth;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidOTPCodeException extends BusinessException {
    public InvalidOTPCodeException() {
        super("INVALID_OTP_CODE", "Mã OTP không đúng");
    }
}
