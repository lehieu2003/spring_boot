package com.example.demo.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper.
 * @param <T> the type of the response data
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  
  private boolean success;
  private String message;
  private T data;
  
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
  
  public ApiResponse(String message, T data) {
    this.success = true;
    this.message = message;
    this.data = data;
    this.timestamp = LocalDateTime.now();
  }
  
  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.timestamp = LocalDateTime.now();
  }
  
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data);
  }
  
  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null);
  }
}
