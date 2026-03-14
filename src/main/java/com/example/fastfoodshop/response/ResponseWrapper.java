package com.example.fastfoodshop.response;

import java.time.Instant;

public record ResponseWrapper<T>(
        boolean success,
        T data,
        String errorCode,
        String message,
        long timestamp
) {
    public static <T> ResponseWrapper<T> success(T data) {
        return new ResponseWrapper<>(true, data, null, null, Instant.now().getEpochSecond());
    }

    public static <T> ResponseWrapper<T> error(String errorCode, String message) {
        return new ResponseWrapper<>(false, null, errorCode, message, Instant.now().getEpochSecond());
    }
}
