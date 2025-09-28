package com.saqib.school.student.model;

import com.saqib.school.student.entity.Student;
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
public class StudentUpdateRequest {

  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  private String firstName;

  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  private String lastName;

  @Pattern(regexp = "^(A|B|AB|O)[+-]?$", message = "Invalid blood group format")
  private String bloodGroup;

  @Size(max = 500, message = "Address must not exceed 500 characters")
  private String address;

  @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{0,20}$", message = "Invalid phone number format")
  private String phone;

  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
  private String emergencyContactName;

  @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{0,20}$", message = "Invalid emergency contact phone format")
  private String emergencyContactPhone;

  @Size(max = 1000, message = "Medical conditions must not exceed 1000 characters")
  private String medicalConditions;

  private String photoUrl;

  private Student.StudentStatus status;
}
