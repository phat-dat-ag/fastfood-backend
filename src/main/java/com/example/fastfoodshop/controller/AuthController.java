package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.*;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseWrapper<OTPResponse>> signUp(@Valid @RequestBody SignUpRequest req) {
        return authService.signUp(req.getName(), req.getPhone(), req.getEmail(), req.getPassword(), req.getBirthdayString());
    }

    @PostMapping("/verify-sign-up")
    public ResponseEntity<ResponseWrapper<UserDTO>> verifyRegistrationOTP(@Valid @RequestBody VerifySignUpRequest req) {
        return authService.verifySignUpOTP(req.getPhone(), req.getOtp());
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseWrapper<SignInResponse>> signIn(@Valid @RequestBody SignInRequest req) {
        return authService.signIn(req.getPhone(), req.getPassword());
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ResponseWrapper<OTPResponse>> forgetPassword(@Valid @RequestBody ForgetPasswordRequest req) {
        return authService.forgetPassword(req.getPhone(), req.getNewPassword());
    }

    @PostMapping("/verify-forget-password")
    public ResponseEntity<ResponseWrapper<UserDTO>> verifyForgetPasswordOTP(@Valid @RequestBody VerifyForgetPasswordReqest req) {
        return authService.verifyForgetPasswordOTP(req.getPhone(), req.getOtp(), req.getNewPassword());
    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseWrapper<SignInResponse>> verify(Authentication authentication) {
        return authService.verify(authentication);
    }
}
