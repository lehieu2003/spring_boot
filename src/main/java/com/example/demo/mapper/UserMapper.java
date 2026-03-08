package com.example.demo.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;

@Component
public class UserMapper {
  
  public UserDTO toDTO(User user) {
    if (user == null) {
      return null;
    }
    return UserDTO.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build();
  }
  
  public User toEntity(UserDTO userDTO) {
    if (userDTO == null) {
      return null;
    }
    User user = new User();
    user.setName(userDTO.getName());
    user.setEmail(userDTO.getEmail());
    return user;
  }
  
  public void updateEntityFromDTO(UserDTO userDTO, User user) {
    if (userDTO == null || user == null) {
      return;
    }
    if (userDTO.getName() != null) {
      user.setName(userDTO.getName());
    }
    if (userDTO.getEmail() != null) {
      user.setEmail(userDTO.getEmail());
    }
  }
}
