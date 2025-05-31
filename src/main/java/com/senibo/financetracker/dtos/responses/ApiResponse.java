package com.senibo.financetracker.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Generic wrapper for all API responses to ensure consistent format.
 *
 * This follows the principle of predictable API design - clients always know
 * what structure to expect, regardless of the endpoint or success/failure state.
 *
 * The @JsonInclude annotation means null fields won't appear in the JSON output,
 * keeping responses clean and avoiding unnecessary data transfer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        LocalDateTime timestamp,
        T data,
        String message,
        ApiError error
) {

    /**
     * Constructor for successful responses with data and custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                true,
                LocalDateTime.now(),
                data,
                message,
                null
        );
    }

    /**
     * Constructor for successful responses with just a message (no data payload)
     * Useful for operations like "User created successfully"
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(
                true,
                LocalDateTime.now(),
                null,
                message,
                null
        );
    }

    /**
     * Constructor for error responses
     */
    public static <T> ApiResponse<T> error(String message, String code) {
        return new ApiResponse<>(
                false,
                LocalDateTime.now(),
                null,
                null,
                new ApiError(code, message)
        );
    }

    /**
     * Constructor for error responses with detailed error information
     */
    public static <T> ApiResponse<T> error(ApiError error) {
        return new ApiResponse<>(
                false,
                LocalDateTime.now(),
                null,
                null,
                error
        );
    }

    /**
     * Nested record for error details.
     * Keeping this as a nested record keeps related concepts together
     * and avoids cluttering your package structure with tiny classes.
     */
    public record ApiError(
            String code,           // Machine-readable error code
            String message,        // Human-readable error message
            Object details         // Additional error context (validation errors, etc.)
    ) {

        /**
         * Simple error without additional details
         */
        public ApiError(String code, String message) {
            this(code, message, null);
        }

        /**
         * Common error codes as constants to avoid typos and ensure consistency
         * This is a pattern you'll see in many APIs - having standard error codes
         */
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
        public static final String DUPLICATE_USERNAME = "DUPLICATE_USERNAME";
        public static final String DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
        public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
        public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
        public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    }
}
