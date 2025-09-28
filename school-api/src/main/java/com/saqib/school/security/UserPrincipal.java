package com.saqib.school.security;

import com.saqib.school.user.entity.User;
import com.saqib.school.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {

  private final Long id;
  private final String username;
  private final String password;
  private final String email;
  private final String firstName;
  private final String lastName;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean enabled;
  private final boolean accountNonLocked;
  private final boolean accountNonExpired;
  private final boolean credentialsNonExpired;

  public static UserPrincipal create(User user) {
    Set<GrantedAuthority> authorities = user.getUserRoles().stream()
      .map(UserRole::getRole)
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
      .collect(Collectors.toSet());

    return new UserPrincipal(
      user.getId(),
      user.getUsername(),
      user.getPasswordHash(),
      user.getEmail(),
      user.getFirstName(),
      user.getLastName(),
      authorities,
      user.isActive(),
      !user.isAccountLocked(),
      true, // accountNonExpired - implement if needed
      true  // credentialsNonExpired - implement if needed
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
