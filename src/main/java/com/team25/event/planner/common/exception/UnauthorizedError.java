package com.team25.event.planner.common.exception;

public class UnauthorizedError extends RuntimeException {
    public UnauthorizedError() {
    }

    public UnauthorizedError(String message) {
        super(message);
    }
}
