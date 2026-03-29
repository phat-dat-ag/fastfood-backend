package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ForgetPasswordRequest;
import com.example.fastfoodshop.request.VerifyForgetPasswordRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.auth.OTPResponse;
import com.example.fastfoodshop.response.auth.VerifyResponse;
import com.example.fastfoodshop.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password-resets")
@RequiredArgsConstructor
public class PasswordResetController extends BaseController {
    private final PasswordResetService passwordResetService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<OTPResponse>> forgetPassword(
            @Valid @RequestBody ForgetPasswordRequest forgetPasswordRequest
    ) {
        return okResponse(passwordResetService.forgetPassword(forgetPasswordRequest));
    }

    @PostMapping("/verification")
    public ResponseEntity<ResponseWrapper<VerifyResponse>> verifyForgetPasswordOTP(
            @Valid @RequestBody VerifyForgetPasswordRequest verifyForgetPasswordRequest
    ) {
        return okResponse(
                passwordResetService.verifyForgetPasswordOTP(verifyForgetPasswordRequest)
        );
    }
}
