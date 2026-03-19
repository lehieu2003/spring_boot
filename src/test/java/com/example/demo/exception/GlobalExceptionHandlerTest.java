package com.example.demo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.example.demo.common.response.ApiResponse;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void shouldMapDuplicateResourceToConflict() {
    var response = handler.handleDuplicateResourceException(
        new DuplicateResourceException("User", "email", "john@example.com"));

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(false, response.getBody().isSuccess());
  }

  @Test
  void shouldMapResourceNotFoundTo404() {
    var response = handler.handleResourceNotFoundException(
        new ResourceNotFoundException("User", "id", 99));

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void shouldHideBadCredentialsDetails() {
    var response = handler.handleBadCredentialsException(
        new org.springframework.security.authentication.BadCredentialsException("Token expired"));

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid credentials", response.getBody().getMessage());
  }

  @Test
  void shouldHideInternalErrorDetails() {
    var response = handler.handleGlobalException(new RuntimeException("sensitive-info"));
    ApiResponse<Object> body = response.getBody();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An internal server error occurred", body.getMessage());
  }
}
