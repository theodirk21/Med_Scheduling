package com.med.scheduling.exception;

import org.springframework.http.HttpStatus;

public class ReminderDayException extends ApplicationException {
    public ReminderDayException(String e) {
        super(HttpStatus.BAD_REQUEST, e);
    }
}
