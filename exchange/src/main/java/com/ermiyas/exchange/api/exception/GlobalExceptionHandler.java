package com.ermiyas.exchange.api.exception;

import com.ermiyas.exchange.api.dto.ExchangeDtos.ErrorResponse;
import com.ermiyas.exchange.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return buildResponse(ex.getMessage(), "INSUFFICIENT_FUNDS", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalBetException.class)
    public ResponseEntity<ErrorResponse> handleIllegalBet(IllegalBetException ex) {
        return buildResponse(ex.getMessage(), "INVALID_BET_STATE", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(ex.getMessage(), "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IdentityConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IdentityConflictException ex) {
        return buildResponse(ex.getMessage(), "IDENTITY_CONFLICT", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExchangeException.class)
    public ResponseEntity<ErrorResponse> handleGeneralExchangeError(ExchangeException ex) {
        return buildResponse(ex.getMessage(), "EXCHANGE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String msg, String code, HttpStatus status) {
        ErrorResponse response = ErrorResponse.builder()
                .message(msg)
                .errorCode(code)
                .timestamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(response, status);
    }
}