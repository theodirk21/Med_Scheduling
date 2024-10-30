package com.med.scheduling.exception;

public class TelegramNotWorkingException extends RuntimeException {
    public TelegramNotWorkingException(String ex) {
        super(ex);
    }
}
