package com.tirana.smartparking.user.dto;

import com.tirana.smartparking.user.entity.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class UserResponseDTO {

    @Setter(AccessLevel.NONE)
    private Long id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<RoleDTO> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String username, String email, String firstName, String lastName, String phoneNumber, Set<RoleDTO> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
