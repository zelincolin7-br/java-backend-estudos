package com.estudos.orderplatform.config;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.estudos.orderplatform.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> 
          errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp: ", Instant.now());
        response.put("status: ", HttpStatus.BAD_REQUEST.value());
        response.put("errors: ", errors);
        log.warn("Validação falhou ao criar/atualizar recurso. errors={}", errors);

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

  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleUnreadableBody(
          org.springframework.http.converter.HttpMessageNotReadableException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", Instant.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "JSON inválido");
    response.put("message",
            "Não foi possível ler o corpo da requisição. "
            + "No pedido, productId pode ser o ID numérico (1) ou o SKU (PROD-1001), "
            + "e quantity deve ser um número inteiro maior que zero.");
    log.warn("Falha ao desserializar JSON: {}", ex.getMostSpecificCause().getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

}