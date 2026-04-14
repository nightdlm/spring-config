package io.spring.config.controller;


import io.spring.config.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@CrossOrigin
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ApiResponse<Map<String,Object>> exceptionHandler(Exception e) {
        logger.error("Exception occurred: {}", e.getMessage(), e);
        return ApiResponse.error(400, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String,Object>> methodArgumentExceptionHandler(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        logger.warn("Validation failed: {}", message);
        return ApiResponse.error(400, message);
    }

}
