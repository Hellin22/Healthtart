package com.dev5ops.healthtart.common.exception;

public class CoolSmsException extends RuntimeException {

    public CoolSmsException(String message) {
        super(message);
    }

    public CoolSmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
