package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private UserRole role;
    private LocalDate birthday;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActivated;
    private boolean isDeleted;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.birthday = user.getBirthday();
        this.avatarUrl = user.getAvatarUrl();
        this.createdAt = user.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.updatedAt = user.getUpdatedAt()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.isActivated = user.isActivated();
        this.isDeleted = user.isDeleted();
    }
}
