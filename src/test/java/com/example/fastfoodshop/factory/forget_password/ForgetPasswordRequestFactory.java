package com.example.fastfoodshop.factory.forget_password;

import com.example.fastfoodshop.request.ForgetPasswordRequest;

public class ForgetPasswordRequestFactory {
    public static ForgetPasswordRequest createValid(String phone) {
        return new ForgetPasswordRequest(phone);
    }
}