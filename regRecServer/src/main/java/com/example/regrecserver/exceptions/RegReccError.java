package com.example.regrecserver.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

@Getter
public enum RegReccError {

    NODE_NOT_FOUND(HttpStatus.NOT_FOUND, "Node not found"),
    NODE_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Name already exists"),
    EDGE_PAIRING_ALREADY_EXISTS(HttpStatus.CONFLICT, "Edge already exists between nodes"),
    EDGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Edge not found"),
    REG_PIECE_WITH_CONTENT_EXISTS(HttpStatus.CONFLICT, "Reg piece with that content already exists"),
    REG_PIECE_NOT_FOUND(HttpStatus.NOT_FOUND, "Reg piece not found"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Missing params");

    private final HttpStatus status;
    private final String message;

    RegReccError(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }
}
