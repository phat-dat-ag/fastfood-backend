package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super("ACCESS_DENIED", message);
    }
}
