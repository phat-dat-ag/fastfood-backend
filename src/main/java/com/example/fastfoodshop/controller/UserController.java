package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.UpdateUserRequest;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/customers")
    public ResponseEntity<ResponseWrapper<ArrayList<UserDTO>>> getAllCustomer() {
        return userService.getAllCustomers();
    }

    @GetMapping("/staff")
    public ResponseEntity<ResponseWrapper<ArrayList<UserDTO>>> getAllStaff() {
        return userService.getAllStaff();
    }

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

    @PutMapping("/manage/activate-account")
    public ResponseEntity<ResponseWrapper<String>> activateAccount(@RequestParam("userId") Long userId) {
        return userService.activateAccount(userId);
    }

    @PutMapping("/manage/deactivate-account")
    public ResponseEntity<ResponseWrapper<String>> deactivateAccount(@RequestParam("userId") Long userId) {
        return userService.deactivateAccount(userId);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<UserDTO>> deleteUser(@RequestParam("phone") String phone) {
        return userService.deleteUser(phone);
    }
}
