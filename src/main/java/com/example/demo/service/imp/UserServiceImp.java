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
    log.info("Creating new user with email: {}", request.getEmail());
    
    // Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateResourceException("User", "email", request.getEmail());
    }
    
    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    User savedUser;
    try {
      savedUser = userRepository.save(user);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateResourceException("User", "email", request.getEmail(), ex);
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
    
    // Check if email is being changed and if new email already exists
    if (request.getEmail() != null
        && !existingUser.getEmail().equals(request.getEmail())
        && userRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateResourceException("User", "email", request.getEmail());
    }
    
    userMapper.updateEntityFromRequest(request, existingUser);
    if (request.getPassword() != null) {
      existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    User updatedUser;
    try {
      updatedUser = userRepository.save(existingUser);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateResourceException("User", "email",
          request.getEmail() != null ? request.getEmail() : existingUser.getEmail(), ex);
    }
    log.info("User updated successfully with ID: {}", id);
    
    return userMapper.toResponse(updatedUser);
  }
  
  @Override
  @Cacheable(cacheNames = CacheConfig.USERS_BY_EMAIL_CACHE, key = "#email")
  public Optional<UserResponse> findByEmail(String email) {
    log.debug("Finding user by email: {}", email);
    
    return userRepository.findByEmail(email)
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
    log.debug("Checking if email exists: {}", email);
    
    return userRepository.existsByEmail(email);
  }
  
  @Override
  @Cacheable(cacheNames = CacheConfig.USERS_ORDERED_CACHE)
  public List<UserResponse> getAllOrderedByName() {
    log.debug("Fetching all users ordered by name");
    
    return userRepository.findAllByOrderByNameAsc().stream()
        .map(userMapper::toResponse)
        .collect(Collectors.toList());
  }
}
