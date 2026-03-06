package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByPhone(String phone);

    User findUserOrThrow(String phone);

    User createUser(String name, String phone, String email, String rawPassword, String birthdayString);

    User updateUser(User user, String name, String phone, String email, String rawPassword, String birthdayString);

    User updateUser(User user, String rawPassword);

    User updateUser(User user);

    ResponseEntity<ResponseWrapper<UserResponse>> getAllCustomers(int page, int size);

    ResponseEntity<ResponseWrapper<UserResponse>> getAllStaff(int page, int size);

    ResponseEntity<ResponseWrapper<UserDTO>> updateUser(String phone, String name, String birthdayString, String email);

    ResponseEntity<ResponseWrapper<UserDTO>> changePassword(String phone, String password, String newPassword);

    ResponseEntity<ResponseWrapper<UserDTO>> updateAvatar(String phone, MultipartFile file);

    ResponseEntity<ResponseWrapper<String>> activateAccount(Long userId);

    ResponseEntity<ResponseWrapper<String>> deactivateAccount(Long userId);

    ResponseEntity<ResponseWrapper<UserDTO>> deleteUser(String phone);
}
