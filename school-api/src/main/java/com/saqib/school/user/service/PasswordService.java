package com.saqib.school.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordService {

  private final PasswordEncoder passwordEncoder;

  @Value("${app.security.password-policy.min-length:8}")
  private int minLength;

  @Value("${app.security.password-policy.require-uppercase:true}")
  private boolean requireUppercase;

  @Value("${app.security.password-policy.require-lowercase:true}")
  private boolean requireLowercase;

  @Value("${app.security.password-policy.require-digits:true}")
  private boolean requireDigits;

  @Value("${app.security.password-policy.require-special-chars:false}")
  private boolean requireSpecialChars;

  public String encodePassword(String rawPassword) {
    validatePassword(rawPassword);
    return passwordEncoder.encode(rawPassword);
  }

  public boolean matchesPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  public void validatePassword(String password) {
    if (password == null || password.length() < minLength) {
      throw new IllegalArgumentException("Password must be at least " + minLength + " characters long");
    }

    if (requireUppercase && !Pattern.compile("[A-Z]").matcher(password).find()) {
      throw new IllegalArgumentException("Password must contain at least one uppercase letter");
    }

    if (requireLowercase && !Pattern.compile("[a-z]").matcher(password).find()) {
      throw new IllegalArgumentException("Password must contain at least one lowercase letter");
    }

    if (requireDigits && !Pattern.compile("[0-9]").matcher(password).find()) {
      throw new IllegalArgumentException("Password must contain at least one digit");
    }

    if (requireSpecialChars && !Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
      throw new IllegalArgumentException("Password must contain at least one special character");
    }
  }

  public boolean isPasswordPolicyCompliant(String password) {
    try {
      validatePassword(password);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
