package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Schema(description = "User entity representing a user in the system")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier of the user", example = "1")
  private Long id;

  @Schema(description = "Name of the user", example = "John Doe")
  private String name;

  @Schema(description = "Email address of the user", example = "john.doe@example.com")
  private String email;

  public User() {
  }


}
