package com.example.regrecserver.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegReccApplicationError extends RuntimeException{
    private RegReccError error;
    private String verboseMessage;
    public RegReccApplicationError(RegReccError exception){
        super(exception.getMessage());
        this.error = exception;
    }

    public RegReccApplicationError(RegReccError exception, String detail){
        super(exception.getMessage());
        this.error = exception;
        this.verboseMessage = detail;
    }
}
