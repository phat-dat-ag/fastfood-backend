package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    protected <T> ResponseEntity<ResponseWrapper<T>> okResponse(T data) {
        return ResponseEntity.ok(ResponseWrapper.success(data));
    }
}
