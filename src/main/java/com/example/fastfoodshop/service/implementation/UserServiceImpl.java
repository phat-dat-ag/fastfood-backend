package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserQueryType;
import com.example.fastfoodshop.enums.UserRole;
import com.example.fastfoodshop.exception.auth.InvalidPasswordException;
import com.example.fastfoodshop.exception.auth.InvalidUserStatusException;
import com.example.fastfoodshop.exception.user.UserNotFoundException;
import com.example.fastfoodshop.repository.UserRepository;
import com.example.fastfoodshop.request.ChangePasswordRequest;
import com.example.fastfoodshop.request.SignUpRequest;
import com.example.fastfoodshop.request.UserUpdateRequest;
import com.example.fastfoodshop.response.user.UserPageResponse;
import com.example.fastfoodshop.response.user.UserResponse;
import com.example.fastfoodshop.response.user.UserUpdateResponse;
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

    private LocalDate parseDate(String date) {
        return LocalDate.parse(date);
    }

    private User buildUser(SignUpRequest signUpRequest) {
        LocalDate birthday = parseDate(signUpRequest.birthdayString());

        User user = new User();

        user.setName(signUpRequest.name());
        user.setPhone(signUpRequest.phone());
        user.setEmail(signUpRequest.email());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.password()));
        user.setBirthday(birthday);
        user.setActivated(false);
        user.setDeleted(false);
        user.setAvatarUrl(null);
        user.setRole(UserRole.USER);

        return user;
    }

    public User createUser(SignUpRequest signUpRequest) {
        User user = buildUser(signUpRequest);

        return userRepository.save(user);
    }

    private void applySignupData(User user, SignUpRequest signUpRequest) {
        LocalDate birthday = parseDate(signUpRequest.birthdayString());

        user.setName(signUpRequest.name());
        user.setPhone(signUpRequest.phone());
        user.setEmail(signUpRequest.email());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.password()));
        user.setBirthday(birthday);
    }

    public User completeRegistration(User user, SignUpRequest signUpRequest) {
        applySignupData(user, signUpRequest);

        return userRepository.save(user);
    }

    private void updateUserPassword(User user, String rawPassword) {
        user.setPasswordHash(passwordEncoder.encode((rawPassword)));
    }

    public void saveUserPassword(User user, String rawPassword) {
        updateUserPassword(user, rawPassword);

        userRepository.save(user);
    }

    public void activateAccount(User user) {
        user.setActivated(true);
        userRepository.save(user);
    }

    private UserRole getUserRole(UserQueryType userQueryType) {
        if (userQueryType == UserQueryType.STAFF) {
            return UserRole.STAFF;
        }

        return UserRole.USER;
    }

    public UserPageResponse getUsers(UserQueryType userQueryType, int page, int size) {
        UserRole userRole = getUserRole(userQueryType);

        Pageable pageable = PageRequest.of(page, size, Sort.by(User.Field.id).ascending());

        Page<User> userPage = userRepository.findByRoleAndIsDeletedFalse(userRole, pageable);

        return UserPageResponse.from(userPage);
    }

    private void updateUserFields(User user, UserUpdateRequest userUpdateRequest) {
        LocalDate birthday = parseDate(userUpdateRequest.birthdayString());

        user.setName(userUpdateRequest.name());
        user.setEmail(userUpdateRequest.email());
        user.setBirthday(birthday);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(UserDTO.from(user));
    }

    public UserResponse updateUser(String phone, UserUpdateRequest userUpdateRequest) {
        User user = findUserOrThrow(phone);

        updateUserFields(user, userUpdateRequest);

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    private void validatePassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }
    }

    public UserResponse changePassword(String phone, ChangePasswordRequest changePasswordRequest) {
        User user = findUserOrThrow(phone);

        validatePassword(user, changePasswordRequest.password());

        updateUserPassword(user, changePasswordRequest.newPassword());

        User updatedUser = userRepository.save(user);
        return toResponse(updatedUser);
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

    public UserResponse updateAvatar(String phone, MultipartFile file) {
        User user = findUserOrThrow(phone);

        handleAvatarImage(user, file);

        User updatedUser = userRepository.save(user);
        return toResponse(updatedUser);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserUpdateResponse updateUserActivation(Long userId, boolean activated) {
        User user = findUserOrThrow(userId);
        if (user.isActivated() == activated) {
            throw new InvalidUserStatusException();
        }

        user.setActivated(activated);
        userRepository.save(user);

        String message = activated
                ? "Kích hoạt tài khoản thành công: " + userId
                : "Hủy kích hoạt tài khoản thành công: " + userId;

        return new UserUpdateResponse(message);
    }

    public UserUpdateResponse deleteUser(Long userId) {
        User user = findUserOrThrow(userId);
        if (user.isDeleted()) {
            throw new InvalidUserStatusException();
        }

        user.setDeleted(true);
        userRepository.save(user);
        return new UserUpdateResponse("Xóa tài khoản thành công: " + userId);
    }
}
