package io.spring.config.controller;


import io.spring.config.response.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@CrossOrigin
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<Map<String,Object>> exceptionHandler(Exception e) {
        return ApiResponse.error(400, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String,Object>> methodArgumentExceptionHandler(MethodArgumentNotValidException e) {
        return ApiResponse.error(400, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

}
