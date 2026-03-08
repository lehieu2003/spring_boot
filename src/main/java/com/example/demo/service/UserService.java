package com.example.demo.service;

import com.example.demo.dto.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations.
 */
public interface UserService extends BaseService<UserDTO, Long> {
  
  /**
   * Find user by email.
   * @param email the email address
   * @return Optional containing the user DTO if found
   */
  Optional<UserDTO> findByEmail(String email);
  
  /**
   * Search users by name.
   * @param name the name to search for
   * @return list of matching user DTOs
   */
  List<UserDTO> searchByName(String name);
  
  /**
   * Check if email exists.
   * @param email the email address
   * @return true if exists, false otherwise
   */
  boolean existsByEmail(String email);
  
  /**
   * Get all users ordered by name.
   * @return list of user DTOs ordered by name
   */
  List<UserDTO> getAllOrderedByName();
}
