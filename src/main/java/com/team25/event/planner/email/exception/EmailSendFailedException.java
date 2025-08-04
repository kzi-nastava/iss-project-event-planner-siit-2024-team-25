package com.team25.event.planner.email.exception;

public class EmailSendFailedException extends RuntimeException {
    public EmailSendFailedException() {
    }

    public EmailSendFailedException(String message) {
        super(message);
    }
}
