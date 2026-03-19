package com.example.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST controller for User management operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
  
  private final UserService userService;

  @PostMapping
  @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
  })
  public ResponseEntity<ApiResponse<UserResponse>> createUser(
      @Valid @RequestBody UserCreateRequest request) {
    UserResponse createdUser = userService.create(request);
    return new ResponseEntity<>(
        new ApiResponse<>("User created successfully", createdUser), 
        HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(
      @Parameter(description = "User ID", required = true)
      @PathVariable Long id) {
    UserResponse user = userService.getById(id);
    return ResponseEntity.ok(
        new ApiResponse<>("User retrieved successfully", user));
  }

  @GetMapping
  @Operation(summary = "Get all users", description = "Retrieves all users in the system")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
    List<UserResponse> users = userService.getAll();
    return ResponseEntity.ok(
        new ApiResponse<>("Users retrieved successfully", users));
  }
  
  @GetMapping("/ordered")
  @Operation(summary = "Get all users ordered by name", description = "Retrieves all users sorted alphabetically by name")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsersOrdered() {
    List<UserResponse> users = userService.getAllOrderedByName();
    return ResponseEntity.ok(
        new ApiResponse<>("Users retrieved successfully", users));
  }
  
  @GetMapping("/search")
  @Operation(summary = "Search users by name", description = "Search users whose names contain the search term")
  public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsersByName(
      @Parameter(description = "Search term for user name")
      @RequestParam String name) {
    List<UserResponse> users = userService.searchByName(name);
    return ResponseEntity.ok(
        new ApiResponse<>("Users found", users));
  }
  
  @GetMapping("/email/{email}")
  @Operation(summary = "Find user by email", description = "Retrieves a user by their email address")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
      @Parameter(description = "User email address", required = true)
      @PathVariable String email) {
    UserResponse user = userService.findByEmail(email)
        .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("User", "email", email));
    return ResponseEntity.ok(
        new ApiResponse<>("User found", user));
  }
  
  @GetMapping("/exists/{email}")
  @Operation(summary = "Check if email exists", description = "Checks whether a user with the given email exists")
  public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
      @Parameter(description = "Email address to check", required = true)
      @PathVariable String email) {
    boolean exists = userService.existsByEmail(email);
    return ResponseEntity.ok(
        new ApiResponse<>("Email existence checked", exists));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update user", description = "Updates an existing user's information")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
  })
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
      @Parameter(description = "User ID", required = true)
      @PathVariable Long id,
      @Valid @RequestBody UserUpdateRequest request) {
    UserResponse updatedUser = userService.update(id, request);
    return ResponseEntity.ok(
        new ApiResponse<>("User updated successfully", updatedUser));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete user", description = "Deletes a user by their ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<ApiResponse<Void>> deleteUser(
      @Parameter(description = "User ID", required = true)
      @PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.ok(
        new ApiResponse<>("User deleted successfully", null));
  }
}
