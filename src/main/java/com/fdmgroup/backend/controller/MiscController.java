package com.fdmgroup.backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
public class MiscController {

    @GetMapping("/hey")
    public String sayHi() {
        return "hi";
    }

    @GetMapping("/csrf")
    @ResponseStatus(HttpStatus.OK)
    public void csrf(HttpServletRequest request) {
        log.debug("GET /csrf from " + request.getRemoteAddr());
    }

}
