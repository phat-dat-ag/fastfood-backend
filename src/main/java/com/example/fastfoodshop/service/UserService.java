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

    //    create new user
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

    //    update a user
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

    public ResponseEntity<ResponseWrapper<UserDTO>> updateUser(String phone, String name, String birthdayString, String email) {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("USER_INVALID", "Không tìm thấy tài khoản"));
        }

        User user = optionalUser.get();
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
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("USER_INVALID", "Không tìm thấy tài khoản"));
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_PASSWORD", "Mật khẩu chưa chính xác"));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(ResponseWrapper.success(new UserDTO(updatedUser)));
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public ResponseEntity<ResponseWrapper<?>> updateAvatar(String phone, MultipartFile file) {
        try {
            Optional<User> optionalUser = userRepository.findByPhone(phone);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error("USER_INVALID", "Không tìm thấy tài khoản"));
            }
            User user = optionalUser.get();
//            update new avatar
            Map<?, ?> result = cloudinaryService.uploadImage(file);
            String avatarUrl = (String) result.get("secure_url");
            String avatarPublicId = (String) result.get("public_id");

            String oldAvatarPublicId = user.getAvatarPublicId();

            user.setAvatarUrl(avatarUrl);
            user.setAvatarPublicId(avatarPublicId);

//            delete current avatar
            if (oldAvatarPublicId != null && !oldAvatarPublicId.isEmpty()) {
                try {
                    boolean isSuccess = cloudinaryService.deleteImage(oldAvatarPublicId);
                    if (isSuccess) {
                        System.out.println("Dọn dẹp ảnh cũ thành công");
                    } else {
                        System.out.println("Dọn dẹp ảnh cũ thất bại, có thể không tìm thấy,...");
                    }
                } catch (Exception e) {
                    System.out.println("Ngoại lệ khi dọn dẹp ảnh cũ: " + e.getMessage());
                }
            }

            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(ResponseWrapper.success(new UserDTO(updatedUser)));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(ResponseWrapper.error("EXCEPTION", "Có ngoại lệ khi cập nhật ảnh đại diện"));
        }
    }
}
