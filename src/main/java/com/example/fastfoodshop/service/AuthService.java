package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.SignInRequest;
import com.example.fastfoodshop.response.auth.SignInResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    SignInResponse signIn(SignInRequest signInRequest);

    SignInResponse verify(Authentication authentication);
}
