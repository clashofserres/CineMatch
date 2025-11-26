package com.clashofserres.cinematch.frontend.core;

import com.clashofserres.cinematch.service.TmdbService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TmdbService.TmdbServiceException.class)
    public ResponseEntity<String> handleMovieNotFound(TmdbService.TmdbServiceException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("TmdbServiceException: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherErrors(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
    }
}
