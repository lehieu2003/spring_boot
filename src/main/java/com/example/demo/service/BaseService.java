package com.example.demo.service;

import java.util.List;

/**
 * Base service interface for common CRUD operations.
 * @param <T> the DTO type
 * @param <ID> the ID type
 */
public interface BaseService<T, ID> {
  
  /**
   * Create a new entity.
   * @param dto the DTO to create
   * @return the created DTO
   */
  T create(T dto);
  
  /**
   * Get entity by ID.
   * @param id the entity ID
   * @return the DTO
   */
  T getById(ID id);
  
  /**
   * Update an existing entity.
   * @param id the entity ID
   * @param dto the DTO with updated data
   * @return the updated DTO
   */
  T update(ID id, T dto);
  
  /**
   * Delete entity by ID.
   * @param id the entity ID
   */
  void delete(ID id);
  
  /**
   * Get all entities.
   * @return list of all DTOs
   */
  List<T> getAll();
}
