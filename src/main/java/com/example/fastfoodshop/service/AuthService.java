package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.*;
import com.example.fastfoodshop.response.SignInResponse;
import com.example.fastfoodshop.response.OTPResponse;
import com.example.fastfoodshop.dto.UserDTO;
import org.springframework.security.core.Authentication;

public interface AuthService {
    OTPResponse signUp(SignUpRequest signUpRequest);

    UserDTO verifySignUpOTP(VerifySignUpRequest verifySignUpRequest);

    SignInResponse signIn(SignInRequest signInRequest);

    OTPResponse forgetPassword(ForgetPasswordRequest forgetPasswordRequest);

    UserDTO verifyForgetPasswordOTP(VerifyForgetPasswordRequest verifyForgetPasswordReqest);

    SignInResponse verify(Authentication authentication);
}
