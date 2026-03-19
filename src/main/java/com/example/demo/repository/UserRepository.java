package com.example.demo.repository;

import com.example.demo.entity.User;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity with custom query methods.
 */
@Repository
public interface UserRepository extends BaseRepository<User, Long> {
  
  /**
   * Find user by email address.
   * @param email the email address
   * @return Optional containing the user if found
   */
  @QueryHints(@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true"))
  Optional<User> findByEmail(String email);
  
  /**
   * Find users by name containing the given string (case-insensitive).
   * @param name the name to search for
   * @return list of matching users
   */
  @QueryHints(@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true"))
  List<User> findByNameContainingIgnoreCase(String name);
  
  /**
   * Check if a user exists with the given email.
   * @param email the email address
   * @return true if user exists, false otherwise
   */
  boolean existsByEmail(String email);
  
  /**
   * Find all users ordered by name.
   * @return list of users ordered by name
   */
  @QueryHints(@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true"))
  List<User> findAllByOrderByNameAsc();
  
  /**
   * Delete user by email address.
   * @param email the email address
   */
  void deleteByEmail(String email);
}
