package com.tirana.smartparking.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private String name;
    private String description;
    private Set<String> permissions;

    public RoleDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
