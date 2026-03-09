package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.SignUpRequest;
import com.example.fastfoodshop.request.UserUpdateRequest;
import com.example.fastfoodshop.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByPhone(String phone);

    User findUserOrThrow(String phone);

    User createUser(SignUpRequest signUpRequest);

    User updateUser(User user, SignUpRequest signUpRequest);

    User updateUser(User user, String rawPassword);

    User updateUser(User user);

    UserResponse getAllCustomers(int page, int size);

    UserResponse getAllStaff(int page, int size);

    UserDTO updateUser(String phone, UserUpdateRequest userUpdateRequest);

    UserDTO changePassword(String phone, ChangePasswordRequest changePasswordRequest);

    UserDTO updateAvatar(String phone, MultipartFile file);

    String activateAccount(Long userId);

    String deactivateAccount(Long userId);

    UserDTO deleteUser(String phone);
}
