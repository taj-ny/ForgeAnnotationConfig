package com.github.taj_ny.forgeannotationconfig.exception;

import lombok.Getter;

/**
 * Thrown whenever a value in the configuration file cannot be assigned to a field.
 */
public class IllegalValueException extends RuntimeException {
    /**
     * The exception that occurred during assigning the value.
     */
    @Getter
    private final Exception originalException;


    public IllegalValueException(Exception originalException, String message) {
        super(message);
        this.originalException = originalException;
    }
}
