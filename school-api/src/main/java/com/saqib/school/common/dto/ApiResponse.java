package com.saqib.school.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private Object errors;
  private LocalDateTime timestamp;

  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
      .success(true)
      .data(data)
      .timestamp(LocalDateTime.now())
      .build();
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
      .success(true)
      .message(message)
      .data(data)
      .timestamp(LocalDateTime.now())
      .build();
  }

  public static <T> ApiResponse<T> error(String message) {
    return ApiResponse.<T>builder()
      .success(false)
      .message(message)
      .timestamp(LocalDateTime.now())
      .build();
  }

  public static <T> ApiResponse<T> error(String message, Object errors) {
    return ApiResponse.<T>builder()
      .success(false)
      .message(message)
      .errors(errors)
      .timestamp(LocalDateTime.now())
      .build();
  }
}
