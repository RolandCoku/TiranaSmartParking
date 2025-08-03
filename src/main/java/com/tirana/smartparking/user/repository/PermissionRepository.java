package com.tirana.smartparking.user.repository;

import com.tirana.smartparking.common.security.PermissionEnum;
import com.tirana.smartparking.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(PermissionEnum name);
}
