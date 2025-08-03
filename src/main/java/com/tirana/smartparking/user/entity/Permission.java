package com.tirana.smartparking.user.entity;

import com.tirana.smartparking.common.security.PermissionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PermissionEnum name;

    public Permission(PermissionEnum name) {
        this.name = name;
    }
}

