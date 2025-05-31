package com.senibo.financetracker.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequest(
        @NotBlank(message = "Username or email is required")
        @Size(max = 50, message = "Username or email must not exceed 50 characters")
        String usernameOrEmail,

        @NotBlank(message = "Password is required")
        String password
) {}
