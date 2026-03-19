package com.example.demo.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.Role;

@Component
public class UserMapper {
  
  public UserResponse toResponse(User user) {
    if (user == null) {
      return null;
    }
    return UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .role(user.getRole() != null ? user.getRole().name() : null)
        .enabled(user.isEnabled())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
  
  public User toEntity(UserCreateRequest request) {
    if (request == null) {
      return null;
    }
    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
    user.setEnabled(request.getEnabled() == null || request.getEnabled());
    return user;
  }
  
  public void updateEntityFromRequest(UserUpdateRequest request, User user) {
    if (request == null || user == null) {
      return;
    }
    if (request.getName() != null) {
      user.setName(request.getName());
    }
    if (request.getEmail() != null) {
      user.setEmail(request.getEmail());
    }
    if (request.getRole() != null) {
      user.setRole(request.getRole());
    }
    if (request.getEnabled() != null) {
      user.setEnabled(request.getEnabled());
    }
  }
}
