package com.smartiadev.auth_service.exception;

import com.smartiadev.auth_service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // erreurs que tu lances toi-même (ex: email déjà utilisé)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {

        ErrorResponse error = new ErrorResponse(ex.getReason());

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(error);
    }

    // toutes les autres erreurs imprévues
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse("Erreur interne du serveur");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}