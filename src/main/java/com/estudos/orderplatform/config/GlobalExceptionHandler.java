package com.estudos.orderplatform.config;

import com.estudos.orderplatform.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Erro de Validação");
        response.put("errors", errors);
        
        log.warn("Validação falhou ao processar requisição. errors={}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Recurso Não Encontrado");
        response.put("message", ex.getMessage());
        
        log.warn("Recurso não encontrado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Conflito de Dados");
        response.put("message", "Já existe um registro cadastrado com estes dados (ex: SKU duplicado).");
        
        log.warn("Conflito de integridade de dados (ex: SKU duplicado).");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "JSON inválido");
        response.put("message", "Não foi possível ler o corpo da requisição. Verifique a sintaxe ou os tipos de dados informados.");
        
        log.warn("Falha ao desserializar JSON: {}", ex.getMostSpecificCause().getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", Instant.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Requisição Inválida");
    response.put("message", ex.getMessage());

    log.warn("Argumento inválido: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
}

@ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", Instant.now());
    response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.put("error", "Erro Interno no Servidor");
    response.put("message", ex.getMessage());

    log.error("Erro não tratado no sistema", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
}
}