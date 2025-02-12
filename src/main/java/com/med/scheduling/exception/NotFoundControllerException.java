package com.med.scheduling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundControllerException extends RuntimeException {
    private final String message;

    public NotFoundControllerException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

