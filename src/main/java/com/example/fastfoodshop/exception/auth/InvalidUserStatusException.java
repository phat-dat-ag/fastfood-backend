package com.example.fastfoodshop.exception.auth;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidUserStatusException extends BusinessException {
    public InvalidUserStatusException(String message) {
        super("INVALID_USER_STATUS", message);
    }
}
