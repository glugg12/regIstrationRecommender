package com.example.regrecserver.handlers;

import com.baeldung.openapi.model.ApiError;
import com.example.regrecserver.exceptions.MissingParamsError;
import com.example.regrecserver.exceptions.RegReccApplicationError;
import com.example.regrecserver.exceptions.RegReccError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class regReccApplicationErrorHandler {
    @ExceptionHandler({RegReccApplicationError.class})
    public ResponseEntity<ApiError> handleRegReccApplicationError(RegReccApplicationError error){
        log.info("Handling error: {}", error.getMessage());
        ApiError apiError = new ApiError();
        apiError.setTitle(error.getError().name());
        apiError.setMessage(error.getMessage());
        apiError.setDetail(error.getVerboseMessage() == null? error.getMessage() : error.getVerboseMessage());
        return new ResponseEntity<>(apiError, error.getError().getStatus());
    }

    @ExceptionHandler({MissingParamsError.class})
    public void handleMissingParams(MissingParamsError error){
        throw new RegReccApplicationError(RegReccError.BAD_REQUEST, error.getMessage());
    }

//    catch(MissingParamsError e)
//    {
//        throw new RegReccApplicationError(RegReccError.BAD_REQUEST, e.getMessage());
//    }
}
