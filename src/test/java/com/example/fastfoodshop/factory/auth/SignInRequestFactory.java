package com.example.fastfoodshop.factory.auth;

import com.example.fastfoodshop.request.SignInRequest;

public class SignInRequestFactory {
    public static final String validPassword = "123456789";

    public static SignInRequest createValid(String phone) {
        return new SignInRequest(phone, validPassword);
    }
}
