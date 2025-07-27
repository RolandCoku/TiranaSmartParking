package com.tirana.smartparking.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserUpdateDTO {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String confirmPassword;

    @Email
    @NotBlank(message = "Email is required!")
    private String email;

    private String phoneNumber;
}


