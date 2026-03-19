package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.example.demo.entity.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "com.example.demo.entity.User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User entity representing a user in the system")
public class User {
  
  // unique identifier for the user
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier of the user", example = "1")
  private Long id;

  @Column(nullable = false, length = 100)
  @Schema(description = "Name of the user", example = "John Doe")
  private String name;

  @Column(nullable = false, unique = true, length = 150)
  @Schema(description = "Email address of the user", example = "john.doe@example.com")
  private String email;

  @Column(nullable = false)
  @Schema(hidden = true)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Schema(description = "Role of the user", example = "USER")
  private Role role;

  @Column(nullable = false)
  @Builder.Default
  @Schema(description = "Whether the user account is enabled")
  private boolean enabled = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Schema(description = "Account creation timestamp")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @Schema(description = "Last update timestamp")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
