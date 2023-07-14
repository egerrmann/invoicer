package com.example.demo.models.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

// The root Exception for HTTP interaction.
// Should be extended by other HTTP headers.
public abstract class HttpException extends RuntimeException {
    @Getter
    @Setter
    HttpStatus status;
    // TODO consider adding the value that will contain the data that caused the exception,
    //  as it could be helpful for the end user.

    public HttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
