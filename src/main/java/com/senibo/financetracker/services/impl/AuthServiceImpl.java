package com.senibo.financetracker.services.impl;

import com.senibo.financetracker.dtos.requests.EmailVerificationRequest;
import com.senibo.financetracker.dtos.requests.SignInRequest;
import com.senibo.financetracker.dtos.requests.SignUpRequest;
import com.senibo.financetracker.dtos.responses.SignInResponse;
import com.senibo.financetracker.dtos.responses.UserResponse;
import com.senibo.financetracker.models.AppRole;
import com.senibo.financetracker.models.Role;
import com.senibo.financetracker.models.User;
import com.senibo.financetracker.repositories.RoleRepository;
import com.senibo.financetracker.repositories.UserRepository;
import com.senibo.financetracker.services.AuthService;
import com.senibo.financetracker.utils.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void registerUser(SignUpRequest signUpRequest) {
        // Debug logging
        System.out.println("SignUpRequest email: [" + signUpRequest.email() + "]");
        System.out.println("Email length: " + signUpRequest.email().length());

        if(userRepository.existsByEmail(signUpRequest.email()))
            throw new IllegalArgumentException("Email address already in use.");

        if(userRepository.existsByUsername(signUpRequest.username()))
            throw new IllegalArgumentException("Username already in use.");

        User user = new User();
        user.setUsername(signUpRequest.username());
        user.setEmail(signUpRequest.email());
        user.setFirstName(signUpRequest.firstName());
        user.setLastName(signUpRequest.lastName());
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));

        // Set security defaults
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false); // Disabled until email verification
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setTwoFactorEnabled(false);
        user.setSignUpMethod("email");

        // Assign default role
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Default user role not found"));
        user.setRole(userRole);

        // Business Logic: Generate verification code
        String verificationCode = generateVerificationCode();
        user.setEmailVerificationCode(verificationCode);
        user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(24));

        // Debug the user object before saving
        System.out.println("User email before save: [" + user.getEmail() + "]");

        userRepository.save(user);

        //Send verification code
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
    }

    @Override
    public void verifyEmail(EmailVerificationRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Business Rule: Can't verify already verified account
        if (user.isEnabled()) {
            throw new IllegalStateException("Account is already verified");
        }

        // Business Rule: Validate verification code
        validateVerificationCode(user, request.verificationCode());

        // Business Logic: Enable the account
        user.setEnabled(true);
        user.setEmailVerificationCode(null);
        user.setEmailVerificationExpiry(null);

        userRepository.save(user);
    }

    @Override
    public SignInResponse authenticateUser(SignInRequest signInRequest) {
        // Business Logic: Find user by username or email
        User user = userRepository.findByUsernameOrEmail(
                        signInRequest.usernameOrEmail(),
                        signInRequest.usernameOrEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        // Business Rule: Account must be verified
        if (!user.isEnabled()) {
            throw new IllegalStateException("Please verify your email before signing in");
        }

        // Security: Authenticate with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        signInRequest.password()));

        if (!authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Convert to response DTO
        UserResponse userResponse = UserResponse.fromUser(user);
        return SignInResponse.success(userResponse);
    }

    @Override
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Business Rule: Can't resend to verified account
        if (user.isEnabled()) {
            throw new IllegalStateException("Account is already verified");
        }

        // Business Logic: Generate new code
        String verificationCode = generateVerificationCode();
        user.setEmailVerificationCode(verificationCode);
        user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);
        emailService.sendVerificationEmail(email, verificationCode);
    }





    private void validateVerificationCode(User user, String verificationCode) {
        if(user.getEmailVerificationCode() == null || !user.getEmailVerificationCode().equals(verificationCode)) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        if(user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired.");
        }
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

}
