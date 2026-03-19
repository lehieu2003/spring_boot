package com.example.demo.service.imp;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private CustomUserDetailsService userDetailsService;

  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void registerShouldThrowConflictWhenEmailAlreadyExists() {
    RegisterRequest request = new RegisterRequest("Alice", "alice@example.com", "secret123");
    when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> authService.register(request));
  }

  @Test
  void registerShouldTranslateUniqueConstraintViolationToConflict() {
    RegisterRequest request = new RegisterRequest("Alice", "alice@example.com", "secret123");

    when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
    when(passwordEncoder.encode("secret123")).thenReturn("hashed");
    when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
        .thenThrow(new DataIntegrityViolationException("duplicate"));

    assertThrows(DuplicateResourceException.class, () -> authService.register(request));
  }

  @Test
  void loginShouldThrowWhenUserNotFoundAfterAuthentication() {
    com.example.demo.dto.LoginRequest request = new com.example.demo.dto.LoginRequest("missing@example.com", "pwd");

    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    assertThrows(com.example.demo.exception.ResourceNotFoundException.class, () -> authService.login(request));
  }
}
