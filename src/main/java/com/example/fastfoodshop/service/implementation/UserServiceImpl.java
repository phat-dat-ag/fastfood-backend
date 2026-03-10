package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;
import com.example.fastfoodshop.exception.auth.InvalidPasswordException;
import com.example.fastfoodshop.exception.auth.InvalidUserStatusException;
import com.example.fastfoodshop.exception.user.UserNotFoundException;
import com.example.fastfoodshop.repository.UserRepository;
import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.SignUpRequest;
import com.example.fastfoodshop.request.UserUpdateRequest;
import com.example.fastfoodshop.response.UserResponse;
import com.example.fastfoodshop.service.CloudinaryService;
import com.example.fastfoodshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public User findUserOrThrow(String phone) {
        return userRepository.findByPhone(phone).orElseThrow(() -> new UserNotFoundException(phone));
    }

    private User findUndeletedUserByIdOrThrow(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
    }

    public User createUser(SignUpRequest signUpRequest) {
        LocalDate birthday = LocalDate.parse(signUpRequest.getBirthdayString());

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setPhone(signUpRequest.getPhone());
        user.setEmail(signUpRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setBirthday(birthday);
        user.setActivated(false);
        user.setDeleted(false);
        user.setAvatarUrl(null);
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    public User updateUser(User user, SignUpRequest signUpRequest) {
        LocalDate birthday = LocalDate.parse(signUpRequest.getBirthdayString());

        user.setName(signUpRequest.getName());
        user.setPhone(signUpRequest.getPhone());
        user.setEmail(signUpRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setBirthday(birthday);
        user.setActivated(false);
        user.setDeleted(false);
        user.setAvatarUrl(null);
        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }

    public User updateUser(User user, String rawPassword) {
        user.setPasswordHash(passwordEncoder.encode((rawPassword)));
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public UserResponse getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> userPage = userRepository.findByRoleAndIsDeletedFalse(UserRole.USER, pageable);

        return new UserResponse(userPage);
    }

    public UserResponse getAllStaff(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> userPage = userRepository.findByRoleAndIsDeletedFalse(UserRole.STAFF, pageable);

        return new UserResponse(userPage);
    }

    public UserDTO updateUser(String phone, UserUpdateRequest userUpdateRequest) {
        User user = findUserOrThrow(phone);
        LocalDate birthday = LocalDate.parse(userUpdateRequest.getBirthdayString());
        user.setName(userUpdateRequest.getName());
        user.setEmail(userUpdateRequest.getEmail());
        user.setBirthday(birthday);

        User updatedUser = userRepository.save(user);
        return UserDTO.from(updatedUser);
    }

    public UserDTO changePassword(String phone, ChangePasswordRequest changePasswordRequest) {
        User user = findUserOrThrow(phone);
        if (!passwordEncoder.matches(changePasswordRequest.getPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }

        user.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        User updatedUser = userRepository.save(user);
        return UserDTO.from(updatedUser);
    }

    public void handleAvatarImage(User user, MultipartFile file) {
        Map<?, ?> result = cloudinaryService.uploadImage(file, "avatar");
        String avatarUrl = (String) result.get("secure_url");
        String avatarPublicId = (String) result.get("public_id");

        String oldAvatarPublicId = user.getAvatarPublicId();

        user.setAvatarUrl(avatarUrl);
        user.setAvatarPublicId(avatarPublicId);

        if (oldAvatarPublicId != null && !oldAvatarPublicId.isEmpty()) {
            try {
                boolean isSuccess = cloudinaryService.deleteImage(oldAvatarPublicId);
                if (isSuccess) {
                    log.info("Old avatar deleted successfully: {}", oldAvatarPublicId);
                }
            } catch (Exception e) {
                log.warn("Failed to delete old avatar: {}", oldAvatarPublicId, e);
            }
        }
    }

    public UserDTO updateAvatar(String phone, MultipartFile file) {
        User user = findUserOrThrow(phone);
        handleAvatarImage(user, file);
        User updatedUser = userRepository.save(user);
        return UserDTO.from(updatedUser);
    }

    public String activateAccount(Long userId) {
        User user = findUndeletedUserByIdOrThrow(userId);
        if (user.isActivated()) {
            throw new InvalidUserStatusException();
        }
        user.setActivated(true);
        userRepository.save(user);
        return "Kích hoạt tài khoản thành công";
    }

    public String deactivateAccount(Long userId) {
        User user = findUndeletedUserByIdOrThrow(userId);
        if (!user.isActivated()) {
            throw new InvalidUserStatusException();
        }
        user.setActivated(false);
        userRepository.save(user);
        return "Hủy kích hoạt tài khoản thành công";
    }

    public UserDTO deleteUser(String phone) {
        User user = findUserOrThrow(phone);
        if (user.isDeleted()) {
            throw new InvalidUserStatusException();
        }
        user.setDeleted(true);
        User deletedUser = userRepository.save(user);
        return UserDTO.from(deletedUser);
    }
}
