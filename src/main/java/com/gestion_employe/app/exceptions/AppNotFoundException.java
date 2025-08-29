package com.gestion_employe.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AppNotFoundException extends Exception{
    public AppNotFoundException(String message) {
        super(message);
    }
    public AppNotFoundException() {
    }
}
