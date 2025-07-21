package com.tirana.smartparking.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {
    private String name;
    private String description;

    public RoleDTO() {
    }
    public RoleDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
