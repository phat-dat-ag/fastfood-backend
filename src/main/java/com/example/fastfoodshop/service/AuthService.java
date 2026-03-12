package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.VerifyForgetPasswordRequest;
import com.example.fastfoodshop.request.ForgetPasswordRequest;
import com.example.fastfoodshop.request.SignInRequest;
import com.example.fastfoodshop.request.SignUpRequest;
import com.example.fastfoodshop.request.VerifySignUpRequest;
import com.example.fastfoodshop.response.auth.SignInResponse;
import com.example.fastfoodshop.response.auth.OTPResponse;
import com.example.fastfoodshop.response.auth.VerifyResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    OTPResponse signUp(SignUpRequest signUpRequest);

    VerifyResponse verifySignUpOTP(VerifySignUpRequest verifySignUpRequest);

    SignInResponse signIn(SignInRequest signInRequest);

    OTPResponse forgetPassword(ForgetPasswordRequest forgetPasswordRequest);

    VerifyResponse verifyForgetPasswordOTP(VerifyForgetPasswordRequest verifyForgetPasswordRequest);

    SignInResponse verify(Authentication authentication);
}
