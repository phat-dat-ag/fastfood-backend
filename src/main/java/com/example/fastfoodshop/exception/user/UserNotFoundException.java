package com.example.fastfoodshop.exception.user;

import com.example.fastfoodshop.exception.base.BusinessException;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String phone) {
        super("USER_NOT_FOUND", "Người dùng không tồn tại: " + phone);
    }

    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "Người dùng không tồn tại hoặc bị xóa: " + userId);
    }
}
