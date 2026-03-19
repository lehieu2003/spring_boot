package com.example.demo.service.imp;

import java.time.Instant;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.Role;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final CustomUserDetailsService userDetailsService;

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String normalizedEmail = normalizeEmail(request.getEmail());
    log.info("Registering new user with email: {}", normalizedEmail);

    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new DuplicateResourceException("User", "email", normalizedEmail);
    }

    User user = User.builder()
        .name(request.getName())
        .email(normalizedEmail)
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .enabled(true)
        .build();

    User savedUser;
    try {
      savedUser = userRepository.save(user);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateResourceException("User", "email", normalizedEmail, ex);
    }
    log.info("User registered successfully with ID: {}", savedUser.getId());

    UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
    String accessToken = jwtService.generateAccessToken(userDetails);
    RefreshToken refreshToken = createRefreshToken(savedUser);

    return buildAuthResponse(savedUser, accessToken, refreshToken.getToken());
  }

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    String normalizedEmail = normalizeEmail(request.getEmail());
    log.info("Login attempt for email: {}", normalizedEmail);

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
      );
    } catch (BadCredentialsException e) {
      throw new BadCredentialsException("Invalid email or password");
    }

    User user = userRepository.findByEmail(normalizedEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User", "email", normalizedEmail));

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String accessToken = jwtService.generateAccessToken(userDetails);

    // Revoke old refresh tokens and create new one
    refreshTokenRepository.deleteByUserId(user.getId());
    RefreshToken refreshToken = createRefreshToken(user);

    log.info("User logged in successfully: {}", user.getEmail());
    return buildAuthResponse(user, accessToken, refreshToken.getToken());
  }

  @Override
  @Transactional
  public AuthResponse refreshToken(String token) {
    log.debug("Refreshing token");

    RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
        .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

    if (refreshToken.isExpired()) {
      refreshTokenRepository.delete(refreshToken);
      throw new BadCredentialsException("Refresh token has expired. Please login again.");
    }

    User user = refreshToken.getUser();
    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String newAccessToken = jwtService.generateAccessToken(userDetails);

    // Rotate refresh token
    refreshTokenRepository.delete(refreshToken);
    RefreshToken newRefreshToken = createRefreshToken(user);

    return buildAuthResponse(user, newAccessToken, newRefreshToken.getToken());
  }

  private RefreshToken createRefreshToken(User user) {
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .token(UUID.randomUUID().toString())
        .expiryDate(Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()))
        .createdAt(Instant.now())
        .build();

    return refreshTokenRepository.save(refreshToken);
  }

  private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .email(user.getEmail())
        .name(user.getName())
        .role(user.getRole().name())
        .build();
  }

  private String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }
}
