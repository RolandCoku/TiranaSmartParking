package com.tirana.smartparking.common.config;

import com.tirana.smartparking.common.security.PermissionEnum;
import com.tirana.smartparking.user.entity.Permission;
import com.tirana.smartparking.user.entity.Role;
import com.tirana.smartparking.user.repository.PermissionRepository;
import com.tirana.smartparking.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * This class seeds the database with initial data such as permissions and roles.
 * It ensures that the necessary permissions are created and assigns them to the ADMIN role.
 */

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;


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
    }
}
