package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.UserUpdateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.user.UserPageResponse;
import com.example.fastfoodshop.response.user.UserResponse;
import com.example.fastfoodshop.response.user.UserUpdateResponse;
import com.example.fastfoodshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;

    @PostMapping("/update-avatar")
    public ResponseEntity<ResponseWrapper<UserResponse>> updateAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(userService.updateAvatar(userDetails.getUsername(), file));
    }

    @PostMapping("/update-information")
    public ResponseEntity<ResponseWrapper<UserResponse>> updateUserInformation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        return okResponse(userService.updateUser(userDetails.getUsername(), userUpdateRequest));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseWrapper<UserResponse>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        return okResponse(userService.changePassword(userDetails.getUsername(), changePasswordRequest));
    }

    @GetMapping("/manage/get/customers")
    public ResponseEntity<ResponseWrapper<UserPageResponse>> getAllCustomer(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(userService.getAllCustomers(request.getPage(), request.getSize()));
    }

    @GetMapping("/manage/get/staff")
    public ResponseEntity<ResponseWrapper<UserPageResponse>> getAllStaff(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(userService.getAllStaff(request.getPage(), request.getSize()));
    }

    @PutMapping("/manage/activate-account")
    public ResponseEntity<ResponseWrapper<UserUpdateResponse>> activateAccount(@RequestParam("userId") Long userId) {
        return okResponse(userService.activateAccount(userId));
    }

    @PutMapping("/manage/deactivate-account")
    public ResponseEntity<ResponseWrapper<UserUpdateResponse>> deactivateAccount(@RequestParam("userId") Long userId) {
        return okResponse(userService.deactivateAccount(userId));
    }

    @DeleteMapping("/manage")
    public ResponseEntity<ResponseWrapper<UserUpdateResponse>> deleteUser(@RequestParam("phone") String phone) {
        return okResponse(userService.deleteUser(phone));
    }
}
