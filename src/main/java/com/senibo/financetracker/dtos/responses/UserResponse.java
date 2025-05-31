package com.senibo.financetracker.dtos.responses;

import com.senibo.financetracker.models.AppRole;
import com.senibo.financetracker.models.User;

import java.time.LocalDateTime;

/**
 * Record for sending user information back to clients.
 *
 * Notice what we include vs exclude:
 * - Include: Information the frontend needs to display or make decisions
 * - Exclude: Sensitive data (password), internal IDs, security flags
 *
 * This demonstrates the principle of "need to know" - only send what the client needs.
 */
public record UserResponse(
        String email,
        String username,
        String firstName,
        String lastName,
        AppRole role,
        boolean isTwoFactorEnabled,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {

    /**
     * Factory method to create UserResponse from User entity.
     * This pattern keeps the conversion logic encapsulated and reusable.
     *
     * Factory methods in records are a clean way to handle object creation
     * when you need to transform data from other objects.
     */
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() != null ? user.getRole().getRoleName() : null,
                user.isTwoFactorEnabled(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );
    }

    /**
     * You might want a version without sensitive timing information
     * for public profiles or search results
     */
    public UserResponse withoutTimestamps() {
        return new UserResponse(
                email, username, firstName, lastName, role, isTwoFactorEnabled, null, null
        );
    }
}
