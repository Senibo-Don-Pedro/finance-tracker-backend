package com.senibo.financetracker.controllers;

import com.senibo.financetracker.dtos.requests.EmailVerificationRequest;
import com.senibo.financetracker.dtos.requests.SignInRequest;
import com.senibo.financetracker.dtos.requests.SignUpRequest;
import com.senibo.financetracker.dtos.responses.ApiResponse;
import com.senibo.financetracker.dtos.responses.SignInResponse;
import com.senibo.financetracker.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> registerUser(
            @Valid
            @RequestBody SignUpRequest signUpRequest
            ){
        try{
            authService.registerUser(signUpRequest);

            return ResponseEntity.ok(
                    ApiResponse.success("User registered successfully! Please check your email for verification code.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(ApiResponse.ApiError.VALIDATION_ERROR, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error(ApiResponse.ApiError.INTERNAL_ERROR, "Registration failed")
            );
        }

    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            authService.verifyEmail(request);
            return ResponseEntity.ok(
                    ApiResponse.success("Email verified successfully! You can now sign in.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(ApiResponse.ApiError.VALIDATION_ERROR, e.getMessage())
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(ApiResponse.ApiError.VALIDATION_ERROR, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error(ApiResponse.ApiError.INTERNAL_ERROR, "Email verification failed")
            );
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponse>> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        try {
            SignInResponse signInResponse = authService.authenticateUser(signInRequest);
            return ResponseEntity.ok(
                    ApiResponse.success(signInResponse, "Sign in successful")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(ApiResponse.ApiError.INVALID_CREDENTIALS, e.getMessage())
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(ApiResponse.ApiError.ACCOUNT_LOCKED, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error(ApiResponse.ApiError.INTERNAL_ERROR, "Sign in failed")
            );
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerificationCode(@RequestParam String email) {
        try {
            authService.resendVerificationCode(email);
            return ResponseEntity.ok(
                    ApiResponse.success("Verification code sent successfully!")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(ApiResponse.ApiError.USER_NOT_FOUND, e.getMessage())
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(ApiResponse.ApiError.VALIDATION_ERROR, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error(ApiResponse.ApiError.INTERNAL_ERROR, "Failed to resend verification code")
            );
        }
    }

}
