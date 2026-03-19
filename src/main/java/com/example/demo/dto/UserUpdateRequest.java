package com.example.demo.dto;

import com.example.demo.entity.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  private String name;

  @Email(message = "Email should be valid")
  private String email;

  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  private String password;

  private Role role;

  private Boolean enabled;
}
