package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.SignUpRequest;
import com.example.fastfoodshop.request.UserUpdateRequest;
import com.example.fastfoodshop.request.VerifySignUpRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.auth.OTPResponse;
import com.example.fastfoodshop.response.auth.VerifyResponse;
import com.example.fastfoodshop.response.promotion.PromotionOrdersResponse;
import com.example.fastfoodshop.response.user.UserResponse;
import com.example.fastfoodshop.service.PromotionService;
import com.example.fastfoodshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;
    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<OTPResponse>> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest
    ) {
        return okResponse(userService.signUp(signUpRequest));
    }

    @PostMapping("/verification")
    public ResponseEntity<ResponseWrapper<VerifyResponse>> verifyRegistrationOTP(
            @Valid @RequestBody VerifySignUpRequest verifySignUpRequest
    ) {
        return okResponse(userService.verifySignUpOTP(verifySignUpRequest));
    }

    @PatchMapping("/me")
    public ResponseEntity<ResponseWrapper<UserResponse>> updateUserInformation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        return okResponse(userService.updateUser(userDetails.getUsername(), userUpdateRequest));
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<ResponseWrapper<UserResponse>> updateAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(userService.updateAvatar(userDetails.getUsername(), file));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ResponseWrapper<UserResponse>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        return okResponse(userService.changePassword(userDetails.getUsername(), changePasswordRequest));
    }

    @GetMapping("/me/promotions/valid")
    public ResponseEntity<ResponseWrapper<PromotionOrdersResponse>> getValidPromotions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(
                promotionService.getValidPromotions(userDetails.getUsername())
        );
    }
}
