package com.example.fastfoodshop.service;

import com.example.fastfoodshop.response.SignInResponse;
import com.example.fastfoodshop.response.OTPResponse;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    ResponseEntity<ResponseWrapper<OTPResponse>> signUp(String name, String phone, String email, String rawPassword, String birthdayString);

    ResponseEntity<ResponseWrapper<UserDTO>> verifySignUpOTP(String phone, String code);

    ResponseEntity<ResponseWrapper<SignInResponse>> signIn(String phone, String password);

    ResponseEntity<ResponseWrapper<OTPResponse>> forgetPassword(String phone, String newPassword);

    ResponseEntity<ResponseWrapper<UserDTO>> verifyForgetPasswordOTP(String phone, String code, String newPassword);

    ResponseEntity<ResponseWrapper<SignInResponse>> verify(Authentication authentication);
}
