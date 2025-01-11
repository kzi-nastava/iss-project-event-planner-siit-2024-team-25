package com.team25.event.planner.common.exception;

public class ReportGenerationFailedException extends RuntimeException {
    public ReportGenerationFailedException(String message) {
        super(message);
    }

    public ReportGenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
