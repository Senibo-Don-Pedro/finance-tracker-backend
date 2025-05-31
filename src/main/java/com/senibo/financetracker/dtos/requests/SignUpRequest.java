package com.senibo.financetracker.dtos.requests;

import com.senibo.financetracker.validation.ValidPassword;
import jakarta.validation.constraints.*;

public record SignUpRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        @Size(max = 50, message = "Email must not exceed 50 characters")
        String email,

        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
        String username,

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @ValidPassword
        String password

) {}
