package com.example.fastfoodshop.factory.user;

import com.example.fastfoodshop.entity.User;

import java.time.Instant;

public class UserFactory {
    private static User createUser() {
        User user = new User();

        user.setId(100L);
        user.setPhone("0989898923");
        user.setPasswordHash("11111111");
        user.setActivated(true);
        user.setDeleted(false);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return user;
    }

    public static User createValidUser() {
        User user = new User();

        user.setId(100L);
        user.setPhone("0989898923");

        return user;
    }

    public static User createActivatedUser() {
        User user = createUser();

        user.setActivated(true);
        user.setDeleted(false);

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
