package com.example.demo.models.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler
    public ResponseError handle(IncorrectDataException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage(), exception.getStatus());
    }
}
