package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserQueryType;
import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.SignUpRequest;
import com.example.fastfoodshop.request.UserUpdateRequest;
import com.example.fastfoodshop.response.user.UserPageResponse;
import com.example.fastfoodshop.response.user.UserResponse;
import com.example.fastfoodshop.response.user.UserStatsResponse;
import com.example.fastfoodshop.response.user.UserUpdateResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByPhone(String phone);

    User findUserOrThrow(String phone);

    User createUser(SignUpRequest signUpRequest);

    User completeRegistration(User user, SignUpRequest signUpRequest);

    void saveUserPassword(User user, String rawPassword);

    void activateAccount(User user);

    UserPageResponse getUsers(UserQueryType userQueryType, int page, int size);

    UserResponse updateUser(String phone, UserUpdateRequest userUpdateRequest);

    UserResponse changePassword(String phone, ChangePasswordRequest changePasswordRequest);

    UserResponse updateAvatar(String phone, MultipartFile file);

    UserUpdateResponse updateUserActivation(Long userId, boolean activated);

    UserUpdateResponse deleteUser(Long userId);

    UserStatsResponse getUserStats();
}
