package com.saqib.school.user.model;

import jakarta.validation.constraints.Email;
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
public class UserUpdateRequest {

  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  private String firstName;

  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  private String lastName;

  @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{0,20}$", message = "Invalid phone number format")
  private String phone;

  @Size(max = 500, message = "Address must not exceed 500 characters")
  private String address;

  private String photoUrl;
}
