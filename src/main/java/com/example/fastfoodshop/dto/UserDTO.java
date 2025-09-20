package com.example.fastfoodshop.dto;


import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDTO {
    private String name;
    private String phone;
    private String email;
    private UserRole role;
    private LocalDate birthday;
    private String avatarUrl;

    public UserDTO(User user) {
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.birthday = user.getBirthday();
        this.avatarUrl = user.getAvatarUrl();
    }
}
