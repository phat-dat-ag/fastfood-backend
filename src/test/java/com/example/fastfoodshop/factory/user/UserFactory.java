package com.example.fastfoodshop.factory.user;

import com.example.fastfoodshop.entity.User;

public class UserFactory {
    public static User createValidUser() {
        User user = new User();

        user.setId(100L);
        user.setPhone("0989898923");

        return user;
    }
}
