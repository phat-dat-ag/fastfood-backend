package com.example.fastfoodshop.exception.otp;

import com.example.fastfoodshop.exception.base.BusinessException;

public class SendOTPException extends BusinessException {
    public SendOTPException(String errorMessage) {
        super("SEND_EMAIL_FAILED", "Lỗi gửi email: " + errorMessage);
    }
}
