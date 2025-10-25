package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;
import com.example.fastfoodshop.repository.UserRepository;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public User findUserOrThrow(String phone) {
        return userRepository.findByPhone(phone).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
    }

    public User createUser(String name, String phone, String email, String rawPassword, String birthdayString) {
        LocalDate birthday = LocalDate.parse(birthdayString);

        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setBirthday(birthday);
        user.setActivated(false);
        user.setDeleted(false);
        user.setAvatarUrl(null);
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    public User updateUser(User user, String name, String phone, String email, String rawPassword, String birthdayString) {
        LocalDate birthday = LocalDate.parse(birthdayString);

        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
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

    public ResponseEntity<ResponseWrapper<ArrayList<UserDTO>>> getAllCustomers() {
        try {
            List<User> users = userRepository.findByRoleAndIsDeletedFalse(UserRole.USER);

            ArrayList<UserDTO> userDTOs = new ArrayList<>();
            for (User user : users) {
                userDTOs.add(new UserDTO(user));
            }

            return ResponseEntity.ok(ResponseWrapper.success(userDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_USER_FAILED",
                    "Lỗi khi lấy các thông tin khách hàng"
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<ArrayList<UserDTO>>> getAllStaff() {
        try {
            List<User> users = userRepository.findByRoleAndIsDeletedFalse(UserRole.STAFF);

            ArrayList<UserDTO> userDTOs = new ArrayList<>();
            for (User user : users) {
                userDTOs.add(new UserDTO(user));
            }

            return ResponseEntity.ok(ResponseWrapper.success(userDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_USER_FAILED",
                    "Lỗi khi lấy các thông tin nhân viên"
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<UserDTO>> updateUser(String phone, String name, String birthdayString, String email) {
        User user = findUserOrThrow(phone);
        LocalDate birthday = LocalDate.parse(birthdayString);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthday);

        try {
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(ResponseWrapper.success(new UserDTO(updatedUser)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("USER_UPDATE_FAILED", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseWrapper<UserDTO>> changePassword(String phone, String password, String newPassword) {
        User user = findUserOrThrow(phone);
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_PASSWORD", "Mật khẩu chưa chính xác"));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(ResponseWrapper.success(new UserDTO(updatedUser)));
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
                    System.out.println("Dọn dẹp ảnh cũ thành công");
                }
            } catch (Exception e) {
                System.out.println("Ngoại lệ khi dọn dẹp ảnh cũ: " + e.getMessage());
            }
        }
    }

    public ResponseEntity<ResponseWrapper<UserDTO>> updateAvatar(String phone, MultipartFile file) {
        try {
            User user = findUserOrThrow(phone);
            handleAvatarImage(user, file);
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(ResponseWrapper.success(new UserDTO(updatedUser)));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(ResponseWrapper.error("EXCEPTION", "Có ngoại lệ khi cập nhật ảnh đại diện"));
        }
    }

    public ResponseEntity<ResponseWrapper<UserDTO>> deleteUser(String phone) {
        try {
            User user = findUserOrThrow(phone);
            user.setDeleted(true);
            User deleteUser = userRepository.save(user);
            return ResponseEntity.ok(ResponseWrapper.success(new UserDTO(deleteUser)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "DELETE_ACCOUNT_FAILED",
                            "Lỗi khi xóa tài khoản " + e.getMessage()
                    )
            );
        }
    }
}
