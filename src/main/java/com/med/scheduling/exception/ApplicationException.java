package com.med.scheduling.exception;

import lombok.Generated;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ApplicationException extends RuntimeException {
    private final HttpStatus statusCode;
    private List<String> details;

    public ApplicationException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApplicationException(HttpStatus statusCode, String message, List<String> details) {
        this(statusCode, message);
        this.details = details;
    }

    @Generated
    public HttpStatus getStatusCode() {
        return this.statusCode;
    }

    @Generated
    public List<String> getDetails() {
        return this.details;
    }
}
