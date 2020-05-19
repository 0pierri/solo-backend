package com.fdmgroup.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends CustomRuntimeException {
    public InvalidRequestException(String msg) {
        super(msg);
    }
}
