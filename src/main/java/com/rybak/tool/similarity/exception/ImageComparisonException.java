package com.rybak.tool.similarity.exception;


public class ImageComparisonException extends RuntimeException {
    public ImageComparisonException(String message) {
        super(message);
    }

    public ImageComparisonException(String message, Throwable cause) {
        super(message, cause);
    }
}
