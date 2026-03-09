package com.example.fastfoodshop.exception.auth;

import com.example.fastfoodshop.exception.base.BusinessException;

public class PhoneAlreadyExistsException extends BusinessException {
    public PhoneAlreadyExistsException(String phone) {
        super("PHONE_ALREADY_EXISTS", "Số điện thoại đã được đăng ký: " + phone);
    }
}
