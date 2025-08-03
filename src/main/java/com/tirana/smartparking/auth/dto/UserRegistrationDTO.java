package com.tirana.smartparking.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {
    private String firstName;
    private String lastName;

    @NotBlank(message = "Username is required!")
    private String username;
    private String password;
    private String confirmPassword;

    @Email
    @NotBlank(message = "Email is required!")
    private String email;

    private String phoneNumber;
}

