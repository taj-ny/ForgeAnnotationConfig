package com.github.taj_ny.forgeannotationconfig.exception;

public class IllegalTypeException extends RuntimeException {
    public IllegalTypeException(Class<?> type) {
        super(String.format("Type %s is not supported. A type adapter is required.", type));
    }
}
