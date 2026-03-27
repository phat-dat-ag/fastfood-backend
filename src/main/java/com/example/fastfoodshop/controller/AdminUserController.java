package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.enums.UserQueryType;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.user.UserPageResponse;
import com.example.fastfoodshop.response.user.UserUpdateResponse;
import com.example.fastfoodshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController extends BaseController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<UserPageResponse>> getAllCustomer(
            @RequestParam(defaultValue = "USER") UserQueryType userQueryType,
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(userService.getUsers(userQueryType, request.getPage(), request.getSize()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserUpdateResponse>> updateUserActivation(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(userService.updateUserActivation(userId, updateActivationRequest.activated()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserUpdateResponse>> deleteUser(
            @PathVariable("id") Long userId
    ) {
        return okResponse(userService.deleteUser(userId));
    }
}
