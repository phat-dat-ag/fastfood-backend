package com.example.fastfoodshop.exception;

import com.example.fastfoodshop.exception.base.BusinessException;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseWrapper<?>> handleBusinessException(BusinessException ex) {

        return ResponseEntity.badRequest()
                .body(ResponseWrapper.error(
                        ex.getErrorCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<?>> handleUnexpectedException(Exception ex) {

        return ResponseEntity.internalServerError()
                .body(ResponseWrapper.error(
                        "INTERNAL_SERVER_ERROR",
                        "Unexpected server error"
                ));
    }
}