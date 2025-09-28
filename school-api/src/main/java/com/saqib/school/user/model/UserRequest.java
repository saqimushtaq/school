package com.saqib.school.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain letters, numbers, dots, hyphens and underscores")
  private String username;

  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name must not exceed 100 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 100, message = "Last name must not exceed 100 characters")
  private String lastName;

  @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{0,20}$", message = "Invalid phone number format")
  private String phone;

  @Size(max = 500, message = "Address must not exceed 500 characters")
  private String address;

  private String photoUrl;
}
