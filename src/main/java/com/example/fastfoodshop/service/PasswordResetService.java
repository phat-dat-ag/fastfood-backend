package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.ForgetPasswordRequest;
import com.example.fastfoodshop.request.VerifyForgetPasswordRequest;
import com.example.fastfoodshop.response.auth.OTPResponse;
import com.example.fastfoodshop.response.auth.VerifyResponse;

public interface PasswordResetService {
    OTPResponse forgetPassword(ForgetPasswordRequest forgetPasswordRequest);

    VerifyResponse verifyForgetPasswordOTP(
            VerifyForgetPasswordRequest verifyForgetPasswordRequest
    );
}