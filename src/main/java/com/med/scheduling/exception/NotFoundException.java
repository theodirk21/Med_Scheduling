package com.med.scheduling.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApplicationException {
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND, "Resource Not Found");
    }
}
