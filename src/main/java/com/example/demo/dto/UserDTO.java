package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
public class UserDTO {
  private String name;
  private String email;

  public UserDTO(String name, String email) {
    this.name = name;
    this.email = email;
  }
}