package com.techelevator.tenmo.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> errors = bindingResult.getFieldErrors();
        String message = "";
        for (FieldError error : errors) {
            message += error.getDefaultMessage();
        }
        return message;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(ResponseStatusException ex) {
        return ex.getReason();
    }

    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(AuthenticationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(DataRetrievalFailureException.class)
    public String handleDataRetrievalFailureException(DataRetrievalFailureException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String handleBadCredentialsException(BadCredentialsException ex) {
        return ex.getMessage();
    }
}
