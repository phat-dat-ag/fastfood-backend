package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.UserUpdateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;
    private final PromotionService promotionService;

    @PatchMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> updateUserInformation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        return okResponse(userService.updateUser(userDetails.getUsername(), userUpdateRequest));
    }

    @PutMapping("/avatar")
    public ResponseEntity<ResponseWrapper<UserResponse>> updateAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(userService.updateAvatar(userDetails.getUsername(), file));
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseWrapper<UserResponse>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        return okResponse(userService.changePassword(userDetails.getUsername(), changePasswordRequest));
    }

    @GetMapping("/promotions/valid")
    public ResponseEntity<ResponseWrapper<PromotionOrdersResponse>> getValidPromotions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(
                promotionService.getValidPromotions(userDetails.getUsername())
        );
    }
}
