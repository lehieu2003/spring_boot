package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "APIs for managing users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ApiResponse<User> createUser(@RequestBody User user) {
    return new ApiResponse<>("User created", userService.create(user));
  }

  @GetMapping("/{id}")
  public ApiResponse<User> getUserById(@PathVariable Long id) {
    return new ApiResponse<>("User found", userService.getById(id));
  }

  @GetMapping
  public ApiResponse<List<User>> getAllUsers() {
    return new ApiResponse<>("All users", userService.getAll());
  }

  @PutMapping("/{id}")
  public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    return new ApiResponse<>("User updated", userService.update(id, user));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return new ApiResponse<>("User deleted", null);
  }

}
