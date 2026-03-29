package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.SignInRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.auth.SignInResponse;
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
@RequestMapping("/api/auth/sessions")
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<SignInResponse>> signIn(
            @Valid @RequestBody SignInRequest signInRequest
    ) {
        return okResponse(authService.signIn(signInRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<SignInResponse>> verify(Authentication authentication) {
        return okResponse(authService.verify(authentication));
    }
}
