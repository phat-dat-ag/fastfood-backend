package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.UpdateUserRequest;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.UserResponse;
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
public class UserController {
    private final UserService userService;

    @PostMapping("/update-avatar")
    public ResponseEntity<ResponseWrapper<UserDTO>> updateAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return userService.updateAvatar(userDetails.getUsername(), file);
    }

    @PostMapping("/update-information")
    public ResponseEntity<ResponseWrapper<UserDTO>> updateUserInformation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateUserRequest information
    ) {
        return userService.updateUser(userDetails.getUsername(), information.getName(), information.getBirthdayString(), information.getEmail());
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseWrapper<UserDTO>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest req
    ) {
        return userService.changePassword(userDetails.getUsername(), req.getPassword(), req.getNewPassword());
    }

    @GetMapping("/manage/get/customers")
    public ResponseEntity<ResponseWrapper<UserResponse>> getAllCustomer(
            @Valid @ModelAttribute PageRequest request
    ) {
        return userService.getAllCustomers(request.getPage(), request.getSize());
    }

    @GetMapping("/manage/get/staff")
    public ResponseEntity<ResponseWrapper<UserResponse>> getAllStaff(
            @Valid @ModelAttribute PageRequest request
    ) {
        return userService.getAllStaff(request.getPage(), request.getSize());
    }

    @PutMapping("/manage/activate-account")
    public ResponseEntity<ResponseWrapper<String>> activateAccount(@RequestParam("userId") Long userId) {
        return userService.activateAccount(userId);
    }

    @PutMapping("/manage/deactivate-account")
    public ResponseEntity<ResponseWrapper<String>> deactivateAccount(@RequestParam("userId") Long userId) {
        return userService.deactivateAccount(userId);
    }

    @DeleteMapping("/manage")
    public ResponseEntity<ResponseWrapper<UserDTO>> deleteUser(@RequestParam("phone") String phone) {
        return userService.deleteUser(phone);
    }
}
