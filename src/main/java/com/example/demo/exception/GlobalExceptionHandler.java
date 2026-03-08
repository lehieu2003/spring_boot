package com.example.demo.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.common.response.ApiResponse;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }
  
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
    ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
    ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }
  
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
    ApiResponse<Object> response = ApiResponse.error("Forbidden: You don't have permission to access this resource");
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    ApiResponse<Map<String, String>> response = new ApiResponse<>(false, "Validation failed", errors);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex) {
    ApiResponse<Object> response = ApiResponse.error("An error occurred: " + ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
