package com.API.BlogV2.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice  // tells to handle exceptions through all controllers
public class GlobalExceptionHandler {



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class) //If a request fails validation because the JSON doesn't meet our @Rules, run this method
    public Map<String,String> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
}
