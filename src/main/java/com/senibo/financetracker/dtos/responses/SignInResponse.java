package com.senibo.financetracker.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response DTO for signin operations.
 *
 * This is designed to evolve naturally when you add JWT tokens later.
 * For now, it focuses on providing the essential information needed
 * after successful authentication.
 *
 * The @JsonInclude annotation ensures that null fields (like the future
 * token field) don't appear in the JSON response until they're actually used.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SignInResponse(
        String message,
        UserResponse user,      // Now THIS is where UserResponse makes sense
        String token,          // Will be null for now, populated when you add JWT
        String refreshToken,   // Future-proofing for when you implement refresh tokens
        Long expiresIn        // Token expiration time in seconds (future use)
) {
    /**
     * Factory method for current simple authentication (without JWT)
     * This gives you a clean way to create responses that will work now
     * and easily extend later when you add token-based auth
     */
    public static SignInResponse success(UserResponse user) {
        return new SignInResponse(
                "Sign in successful",
                user,
                null,  // No token yet
                null,  // No refresh token yet
                null   // No expiration yet
        );
    }

    /**
     * Factory method for full JWT implementation with refresh tokens (future use)
     */
    public static SignInResponse successWithTokens(UserResponse user, String token, String refreshToken, Long expiresIn) {
        return new SignInResponse(
                "Sign in successful",
                user,
                token,
                refreshToken,
                expiresIn
        );
    }
}
