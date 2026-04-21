package com.example.fastfoodshop.factory.forget_password;

import com.example.fastfoodshop.request.VerifyForgetPasswordRequest;

public class VerifyForgetPasswordRequestFactory {
    private static final String PASSWORD = "123321";

    public static VerifyForgetPasswordRequest createValid(String phone, String otpCode) {
        return new VerifyForgetPasswordRequest(phone, otpCode, PASSWORD);
    }
}