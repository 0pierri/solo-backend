package com.fdmgroup.backend.exception;

public abstract class CustomRuntimeException extends RuntimeException {
    public CustomRuntimeException(String msg) {
        super(msg);
    }
//    @Override
//    public synchronized Throwable fillInStackTrace() {
//        return this;
//    }
}
