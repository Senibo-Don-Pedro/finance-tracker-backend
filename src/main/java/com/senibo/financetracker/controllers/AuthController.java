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
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        // With global exception handler, this becomes much simpler!
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(
                ApiResponse.success("User registered successfully! Please check your email for verification code.")
        );
    }


    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(
                ApiResponse.success("Email verified successfully! You can now sign in.")
        );
    }


    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponse>> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        SignInResponse signInResponse = authService.authenticateUser(signInRequest);
        return ResponseEntity.ok(
                ApiResponse.success(signInResponse, "Sign in successful")
        );
    }


    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerificationCode(@RequestParam String email) {
        authService.resendVerificationCode(email);
        return ResponseEntity.ok(
                ApiResponse.success("Verification code sent successfully!")
        );
    }


}
