package com.example.fastfoodshop.factory.user;

import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;

import java.time.Instant;

public class UserFactory {
    private static User createUser() {
        User user = new User();

        user.setId(100L);
        user.setEmail("dat@gmail.com");
        user.setPhone("0989898923");
        user.setPasswordHash("11111111");
        user.setActivated(true);
        user.setDeleted(false);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return user;
    }

    public static User createActivatedUser() {
        User user = createUser();

        user.setActivated(true);
        user.setDeleted(false);

        return user;
    }

    public static User createActivatedUserWithRole(Long userId, UserRole userRole) {
        User user = createActivatedUser();

        user.setId(userId);
        user.setRole(userRole);

        return user;
    }

    public static User createDeactivatedUser() {
        User user = createUser();

        user.setActivated(false);
        user.setDeleted(false);

        return user;
    }

    public static User createDeletedUser() {
        User user = createUser();

        user.setActivated(false);
        user.setDeleted(true);

        return user;
    }
}
