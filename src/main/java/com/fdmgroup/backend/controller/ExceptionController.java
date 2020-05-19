package com.fdmgroup.backend.controller;

import com.fdmgroup.backend.exception.ErrorPathException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestController
public class ExceptionController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public ExceptionController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public Map<String, Object> handleException(WebRequest request) {
        Map<String, Object> ea = errorAttributes.getErrorAttributes(request, false);
        // If client requested /error
        if (ea.get("status").equals(999)) {
            throw new ErrorPathException("Tried to GET /error, received /tea instead");
        }
        return ea;
    }
}
