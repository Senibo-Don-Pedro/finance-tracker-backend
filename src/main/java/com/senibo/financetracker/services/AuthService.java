package com.senibo.financetracker.services;

import com.senibo.financetracker.dtos.requests.EmailVerificationRequest;
import com.senibo.financetracker.dtos.requests.SignInRequest;
import com.senibo.financetracker.dtos.requests.SignUpRequest;
import com.senibo.financetracker.dtos.responses.SignInResponse;

public interface AuthService {
    void registerUser(SignUpRequest signUpRequest);
    void verifyEmail(EmailVerificationRequest request);
    SignInResponse authenticateUser(SignInRequest signInRequest);
    void resendVerificationCode(String email);
}
