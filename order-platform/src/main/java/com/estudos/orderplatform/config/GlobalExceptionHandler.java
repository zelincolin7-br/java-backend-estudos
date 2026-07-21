package com.estudos.orderplatform.config;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.estudos.orderplatform.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> 
          errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp: ", Instant.now());
        response.put("status: ", HttpStatus.BAD_REQUEST.value());
        response.put("errors: ", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", Instant.now());
    response.put("status", HttpStatus.NOT_FOUND.value());
    response.put("error", "Recurso Não Encontrado");
    response.put("message", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }


}