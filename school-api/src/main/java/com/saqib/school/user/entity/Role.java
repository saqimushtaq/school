package com.saqib.school.user.entity;

import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Role extends BaseEntity {

  @Column(name = "role_name", unique = true, nullable = false, length = 50)
  private String roleName;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;

  @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<UserRole> userRoles;
}
