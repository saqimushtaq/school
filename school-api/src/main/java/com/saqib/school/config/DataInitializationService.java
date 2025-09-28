package com.saqib.school.config;

import com.saqib.school.user.entity.Role;
import com.saqib.school.user.entity.User;
import com.saqib.school.user.entity.UserRole;
import com.saqib.school.user.repository.RoleRepository;
import com.saqib.school.user.repository.UserRepository;
import com.saqib.school.user.repository.UserRoleRepository;
import com.saqib.school.user.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordService passwordService;

    @Override
    @Transactional
    public void run(String... args) {
        initializeRoles();
        initializeDefaultAdmin();
        log.info("Data initialization completed successfully");
    }

    private void initializeRoles() {
        List<String> defaultRoles = Arrays.asList(
                "PRINCIPAL",
                "ADMIN_OFFICER",
                "ACCOUNTANT",
                "CLASS_TEACHER",
                "SUBJECT_TEACHER",
                "RECEPTION",
                "IT_ADMIN"
        );

        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = Role.builder()
                        .roleName(roleName)
                        .description(getDefaultRoleDescription(roleName))
                        .isActive(true)
                        .build();

                roleRepository.save(role);
                log.info("Created default role: {}", roleName);
            }
        }
    }

    private void initializeDefaultAdmin() {
        String adminUsername = "admin";

        if (!userRepository.existsByUsername(adminUsername)) {
            // Create admin user
            User adminUser = User.builder()
                    .username(adminUsername)
                    .email("admin@school.com")
                    .passwordHash(passwordService.encodePassword("Admin123"))
                    .firstName("System")
                    .lastName("Administrator")
                    .status(User.UserStatus.ACTIVE)
                    .mustChangePassword(true)
                    .build();

            User savedAdmin = userRepository.save(adminUser);
            log.info("Created default admin user: {}", adminUsername);

            // Assign PRINCIPAL and IT_ADMIN roles to admin
            Role principalRole = roleRepository.findByRoleName("PRINCIPAL").orElse(null);
            Role itAdminRole = roleRepository.findByRoleName("IT_ADMIN").orElse(null);

            if (principalRole != null) {
                UserRole principalUserRole = UserRole.builder()
                        .user(savedAdmin)
                        .role(principalRole)
                        .assignedBy(savedAdmin)
                        .build();
                userRoleRepository.save(principalUserRole);
                log.info("Assigned PRINCIPAL role to admin user");
            }

            if (itAdminRole != null) {
                UserRole itAdminUserRole = UserRole.builder()
                        .user(savedAdmin)
                        .role(itAdminRole)
                        .assignedBy(savedAdmin)
                        .build();
                userRoleRepository.save(itAdminUserRole);
                log.info("Assigned IT_ADMIN role to admin user");
            }
        }
    }

    private String getDefaultRoleDescription(String roleName) {
        return switch (roleName) {
            case "PRINCIPAL" -> "Full system access - School Principal";
            case "ADMIN_OFFICER" -> "Student management, fee collection, general administration";
            case "ACCOUNTANT" -> "Financial management, fee reports, expense tracking";
            case "CLASS_TEACHER" -> "Attendance marking, marks entry for assigned class";
            case "SUBJECT_TEACHER" -> "Marks entry for assigned subjects only";
            case "RECEPTION" -> "Inquiry management, basic student information";
            case "IT_ADMIN" -> "System configuration, user management, backups";
            default -> "System role";
        };
    }
}
