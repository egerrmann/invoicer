package com.example.demo.models.exceptions;

import org.springframework.http.HttpStatus;

public class IncorrectDataException extends HttpException {
    public IncorrectDataException(String message, HttpStatus status) {
        super(message, status);
    }
}
