package com.example.fastfoodshop.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResponseWrapper<T> {
    private boolean success;
    private T data;
    private String errorCode;
    private String message;
    private long timestamp;

    public ResponseWrapper(boolean success, T data, String errorCode, String message) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = Instant.now().getEpochSecond();
    }

    public static <T> ResponseWrapper<T> success(T data) {
        return new ResponseWrapper<>(true, data, null, null);
    }

    public static <T> ResponseWrapper<T> error(String errorCode, String message) {
        return new ResponseWrapper<>(false, null, errorCode, message);
    }
}
