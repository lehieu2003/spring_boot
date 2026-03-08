package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for User")
public class UserDTO {
  
  @Schema(description = "User ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;
  
  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "User's full name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;
  
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Schema(description = "User's email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @Schema(description = "User's role", example = "USER", accessMode = Schema.AccessMode.READ_ONLY)
  private String role;
}