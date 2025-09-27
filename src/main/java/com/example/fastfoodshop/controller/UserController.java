package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.ChangePasswordRequest;
import com.example.fastfoodshop.dto.UpdateUserRequest;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/update-avatar")
    public ResponseEntity<ResponseWrapper<?>> updateAvatar(
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
}
