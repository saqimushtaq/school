package com.saqib.school.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {

  @NotBlank(message = "Role name is required")
  @Size(max = 50, message = "Role name must not exceed 50 characters")
  private String roleName;

  @Size(max = 500, message = "Description must not exceed 500 characters")
  private String description;
}
