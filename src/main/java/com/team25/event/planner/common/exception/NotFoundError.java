package com.team25.event.planner.common.exception;

public class NotFoundError extends RuntimeException {
    public NotFoundError() {
    }

    public NotFoundError(String message) {
        super(message);
    }
}
