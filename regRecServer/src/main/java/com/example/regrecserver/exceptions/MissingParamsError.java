package com.example.regrecserver.exceptions;

public class MissingParamsError extends Exception{
    public MissingParamsError(String errorMessage) { super(errorMessage); }
}
