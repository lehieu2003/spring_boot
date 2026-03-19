package com.example.demo.service.imp;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.CacheConfig;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for User operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {
  
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  @Caching(evict = {
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_ID_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_EMAIL_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_ALL_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_ORDERED_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_NAME_CACHE, allEntries = true)
  })
  public UserResponse create(UserCreateRequest request) {
    String normalizedEmail = normalizeEmail(request.getEmail());
    log.info("Creating new user with email: {}", normalizedEmail);
    
    // Check if email already exists
    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new DuplicateResourceException("User", "email", normalizedEmail);
    }
    
    User user = userMapper.toEntity(request);
    user.setEmail(normalizedEmail);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    User savedUser;
    try {
      savedUser = userRepository.save(user);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateResourceException("User", "email", normalizedEmail, ex);
    }
    log.info("User created successfully with ID: {}", savedUser.getId());
    
    return userMapper.toResponse(savedUser);
  }

  @Override
  @Cacheable(cacheNames = CacheConfig.USERS_BY_ID_CACHE, key = "#id")
  public UserResponse getById(Long id) {
    log.debug("Fetching user with ID: {}", id);
    
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    
    return userMapper.toResponse(user);
  }

  @Override
  @Cacheable(cacheNames = CacheConfig.USERS_ALL_CACHE)
  public List<UserResponse> getAll() {
    log.debug("Fetching all users");
    
    return userRepository.findAll().stream()
        .map(userMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  @Caching(evict = {
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_ID_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_EMAIL_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_ALL_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_ORDERED_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_NAME_CACHE, allEntries = true)
  })
  public void delete(Long id) {
    log.info("Deleting user with ID: {}", id);
    
    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException("User", "id", id);
    }
    
    userRepository.deleteById(id);
    log.info("User deleted successfully with ID: {}", id);
  }

  @Override
  @Transactional
  @Caching(evict = {
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_ID_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_EMAIL_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_ALL_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_ORDERED_CACHE, allEntries = true),
      @CacheEvict(cacheNames = CacheConfig.USERS_BY_NAME_CACHE, allEntries = true)
  })
  public UserResponse update(Long id, UserUpdateRequest request) {
    log.info("Updating user with ID: {}", id);
    
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    
    String normalizedEmail = request.getEmail() != null ? normalizeEmail(request.getEmail()) : null;

    // Check if email is being changed and if new email already exists
    if (normalizedEmail != null
        && !existingUser.getEmail().equals(normalizedEmail)
        && userRepository.existsByEmail(normalizedEmail)) {
      throw new DuplicateResourceException("User", "email", normalizedEmail);
    }
    
    userMapper.updateEntityFromRequest(request, existingUser);
    if (normalizedEmail != null) {
      existingUser.setEmail(normalizedEmail);
    }
    if (request.getPassword() != null) {
      existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    User updatedUser;
    try {
      updatedUser = userRepository.save(existingUser);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateResourceException("User", "email",
          normalizedEmail != null ? normalizedEmail : existingUser.getEmail(), ex);
    }
    log.info("User updated successfully with ID: {}", id);
    
    return userMapper.toResponse(updatedUser);
  }
  
  @Override
  @Cacheable(cacheNames = CacheConfig.USERS_BY_EMAIL_CACHE, key = "#email")
  public Optional<UserResponse> findByEmail(String email) {
    String normalizedEmail = normalizeEmail(email);
    log.debug("Finding user by email: {}", normalizedEmail);
    
    return userRepository.findByEmail(normalizedEmail)
        .map(userMapper::toResponse);
  }
  
  @Override
  @Cacheable(cacheNames = CacheConfig.USERS_BY_NAME_CACHE, key = "#name")
  public List<UserResponse> searchByName(String name) {
    log.debug("Searching users by name: {}", name);
    
    return userRepository.findByNameContainingIgnoreCase(name).stream()
        .map(userMapper::toResponse)
        .collect(Collectors.toList());
  }
  
  @Override
  public boolean existsByEmail(String email) {
    String normalizedEmail = normalizeEmail(email);
    log.debug("Checking if email exists: {}", normalizedEmail);
    
    return userRepository.existsByEmail(normalizedEmail);
  }
  
  @Override
  @Cacheable(cacheNames = CacheConfig.USERS_ORDERED_CACHE)
  public List<UserResponse> getAllOrderedByName() {
    log.debug("Fetching all users ordered by name");
    
    return userRepository.findAllByOrderByNameAsc().stream()
        .map(userMapper::toResponse)
        .collect(Collectors.toList());
  }

  private String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }
}
