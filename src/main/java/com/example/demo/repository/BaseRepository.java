package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for generic CRUD operations.
 * NoRepositoryBean prevents Spring from creating a repository bean for this interface.
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
  // JpaRepository already provides all necessary CRUD methods
  // Add custom common methods here if needed across all repositories
}
