package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;
import com.example.fastfoodshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
