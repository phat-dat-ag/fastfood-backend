package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record UserDTO(
        Long id,
        String name,
        String phone,
        String email,
        UserRole role,
        LocalDate birthday,
        String avatarUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean activated,
        boolean deleted
) {
    public static UserDTO from(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getRole(),
                user.getBirthday(),
                user.getAvatarUrl(),
                user.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                user.getUpdatedAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                user.isActivated(),
                user.isDeleted()
        );
    }
}
