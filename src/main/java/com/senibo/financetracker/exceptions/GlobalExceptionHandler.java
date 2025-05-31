
package com.senibo.financetracker.exceptions;

import com.senibo.financetracker.dtos.responses.ApiResponse;
import com.senibo.financetracker.dtos.responses.ApiResponse.ApiError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @Valid annotations on request DTOs
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ApiError error = new ApiError(
                ApiError.VALIDATION_ERROR,
                "Request validation failed",
                validationErrors
        );

        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    /**
     * Handles JPA/Entity validation errors (like your current issue)
     * This catches validation errors when saving to database
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> validationErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            validationErrors.put(fieldName, message);
        });

        ApiError error = new ApiError(
                ApiError.VALIDATION_ERROR,
                "Data validation failed",
                validationErrors
        );

        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    /**
     * Handles database constraint violations (unique constraints, etc.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        String message = "Data integrity violation";
        String code = ApiError.VALIDATION_ERROR;

        // Check for common constraint violations
        if (ex.getMessage().contains("email")) {
            message = "Email address already exists";
            code = ApiError.DUPLICATE_EMAIL;
        } else if (ex.getMessage().contains("username")) {
            message = "Username already exists";
            code = ApiError.DUPLICATE_USERNAME;
        }

        ApiError error = new ApiError(code, message);
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    /**
     * Handles business logic validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiError error = new ApiError(ApiError.VALIDATION_ERROR, ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    /**
     * Handles state-related errors
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        ApiError error = new ApiError(ApiError.ACCOUNT_LOCKED, ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    /**
     * Catches any other unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        ApiError error = new ApiError(
                ApiError.INTERNAL_ERROR,
                "An unexpected error occurred",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }
}
