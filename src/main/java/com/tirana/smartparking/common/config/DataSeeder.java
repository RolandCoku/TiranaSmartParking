package com.tirana.smartparking.common.config;

import com.tirana.smartparking.common.security.PermissionEnum;
import com.tirana.smartparking.user.entity.Permission;
import com.tirana.smartparking.user.entity.Role;
import com.tirana.smartparking.user.repository.PermissionRepository;
import com.tirana.smartparking.user.repository.RoleRepository;
import com.tirana.smartparking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * This class seeds the database with initial data such as permissions and roles.
 * It ensures that the necessary permissions are created and assigns them to the ADMIN role.
 */

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        // Seed permissions
        for (PermissionEnum perm : PermissionEnum.values()) {
            permissionRepository.findByName(perm)
                    .orElseGet(() -> permissionRepository.save(new Permission(perm)));
        }

        // Create an ADMIN role with all permissions
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role admin = new Role();
            admin.setName("ADMIN");
            admin.setPermissions(new HashSet<>(permissionRepository.findAll()));
            roleRepository.save(admin);
        }

        // Create a USER role with only the READ permission
        if (roleRepository.findByName("USER").isEmpty()) {
            Role role = new Role();
            role.setName("USER");
            role.setDescription("Regular user role with limited permissions");
            roleRepository.save(role);
        }

        // Create a admin user if it does not exist with ADMIN role
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Create a new user with ADMIN role
            com.tirana.smartparking.user.entity.User adminUser = new com.tirana.smartparking.user.entity.User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // In a real application, ensure to hash the password
            adminUser.setRoles(Set.of(roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("ADMIN role not found"))));
            adminUser.setEmail("admin@admin.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setPhoneNumber("1234567890");
            userRepository.save(adminUser);
        }
    }
}
