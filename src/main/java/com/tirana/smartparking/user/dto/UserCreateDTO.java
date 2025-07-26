package com.tirana.smartparking.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserCreateDTO {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String confirmPassword;

    @Email
    @NotBlank
    private String email;

    private String phoneNumber;
    private Set<String> roles;

    public UserCreateDTO() {
    }

    public UserCreateDTO(String firstName, String lastName, String username, String password, String confirmPassword, String email, String phoneNumber, Set<String> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }
}


