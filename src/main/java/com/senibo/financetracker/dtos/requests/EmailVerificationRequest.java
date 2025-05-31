package com.senibo.financetracker.dtos.requests;

import jakarta.validation.constraints.*;

public record EmailVerificationRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Verification code is required")
        @Pattern(regexp = "\\d{6}", message = "Verification code must be 6 digits")
        String verificationCode

) {
}
