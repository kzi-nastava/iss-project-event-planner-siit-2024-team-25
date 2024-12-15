package com.team25.event.planner.user.exception;

public class UnauthenticatedError extends RuntimeException {
    public UnauthenticatedError(String message) {
        super(message);
    }
}
