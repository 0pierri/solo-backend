package com.fdmgroup.backend.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({InvalidRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleError(InvalidRequestException e) {
        log.info(e.getMessage());
        log.debug(e.getStackTrace());
        return e.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleError(NotFoundException e) {
        log.info(e.getMessage());
        log.debug(e.getStackTrace());
        return e.getMessage();
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(ServerException e) {
        log.info(e.getMessage());
        log.debug(e.getStackTrace());
        return "Something went wrong";
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleError(HttpMessageNotReadableException e) {
        log.info(e.getMessage());
        log.debug(e.getStackTrace());
        return "Could not parse request parameters";
    }

}
