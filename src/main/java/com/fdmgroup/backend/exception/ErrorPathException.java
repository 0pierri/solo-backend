package com.fdmgroup.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class ErrorPathException extends CustomRuntimeException {
    public ErrorPathException(String msg) {
        super(msg);
    }
}
