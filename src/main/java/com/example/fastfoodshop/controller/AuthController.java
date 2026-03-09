package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.request.ForgetPasswordRequest;
import com.example.fastfoodshop.request.VerifyForgetPasswordRequest;
import com.example.fastfoodshop.request.SignInRequest;
import com.example.fastfoodshop.request.SignUpRequest;
import com.example.fastfoodshop.request.VerifySignUpRequest;
import com.example.fastfoodshop.response.OTPResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.SignInResponse;
import com.example.fastfoodshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseWrapper<OTPResponse>> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest
    ) {
        return okResponse(authService.signUp(signUpRequest));
    }

    @PostMapping("/verify-sign-up")
    public ResponseEntity<ResponseWrapper<UserDTO>> verifyRegistrationOTP(
            @Valid @RequestBody VerifySignUpRequest verifySignUpRequest
    ) {
        return okResponse(authService.verifySignUpOTP(verifySignUpRequest));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseWrapper<SignInResponse>> signIn(
            @Valid @RequestBody SignInRequest signInRequest
    ) {
        return okResponse(authService.signIn(signInRequest));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ResponseWrapper<OTPResponse>> forgetPassword(
            @Valid @RequestBody ForgetPasswordRequest forgetPasswordRequest
    ) {
        return okResponse(authService.forgetPassword(forgetPasswordRequest));
    }

    @PostMapping("/verify-forget-password")
    public ResponseEntity<ResponseWrapper<UserDTO>> verifyForgetPasswordOTP(
            @Valid @RequestBody VerifyForgetPasswordRequest verifyForgetPasswordRequest
    ) {
        return okResponse(authService.verifyForgetPasswordOTP(verifyForgetPasswordRequest));
    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseWrapper<SignInResponse>> verify(Authentication authentication) {
        return okResponse(authService.verify(authentication));
    }
}
