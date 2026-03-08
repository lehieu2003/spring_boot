package com.example.demo.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

  @Override
  @Transactional
  public UserDTO create(UserDTO userDTO) {
    log.info("Creating new user with email: {}", userDTO.getEmail());
    
    // Check if email already exists
    if (userRepository.existsByEmail(userDTO.getEmail())) {
      throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
    }
    
    User user = userMapper.toEntity(userDTO);
    User savedUser = userRepository.save(user);
    log.info("User created successfully with ID: {}", savedUser.getId());
    
    return userMapper.toDTO(savedUser);
  }

  @Override
  public UserDTO getById(Long id) {
    log.debug("Fetching user with ID: {}", id);
    
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    
    return userMapper.toDTO(user);
  }

  @Override
  public List<UserDTO> getAll() {
    log.debug("Fetching all users");
    
    return userRepository.findAll().stream()
        .map(userMapper::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
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
  public UserDTO update(Long id, UserDTO userDTO) {
    log.info("Updating user with ID: {}", id);
    
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    
    // Check if email is being changed and if new email already exists
    if (!existingUser.getEmail().equals(userDTO.getEmail()) 
        && userRepository.existsByEmail(userDTO.getEmail())) {
      throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
    }
    
    userMapper.updateEntityFromDTO(userDTO, existingUser);
    User updatedUser = userRepository.save(existingUser);
    log.info("User updated successfully with ID: {}", id);
    
    return userMapper.toDTO(updatedUser);
  }
  
  @Override
  public Optional<UserDTO> findByEmail(String email) {
    log.debug("Finding user by email: {}", email);
    
    return userRepository.findByEmail(email)
        .map(userMapper::toDTO);
  }
  
  @Override
  public List<UserDTO> searchByName(String name) {
    log.debug("Searching users by name: {}", name);
    
    return userRepository.findByNameContainingIgnoreCase(name).stream()
        .map(userMapper::toDTO)
        .collect(Collectors.toList());
  }
  
  @Override
  public boolean existsByEmail(String email) {
    log.debug("Checking if email exists: {}", email);
    
    return userRepository.existsByEmail(email);
  }
  
  @Override
  public List<UserDTO> getAllOrderedByName() {
    log.debug("Fetching all users ordered by name");
    
    return userRepository.findAllByOrderByNameAsc().stream()
        .map(userMapper::toDTO)
        .collect(Collectors.toList());
  }
}
