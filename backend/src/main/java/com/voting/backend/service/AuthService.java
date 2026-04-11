package com.voting.backend.service;

import com.voting.backend.dto.request.LoginRequest;
import com.voting.backend.dto.request.RegisterRequest;
import com.voting.backend.dto.request.ResetPasswordRequest;
import com.voting.backend.dto.response.AuthResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Role;
import com.voting.backend.model.Voter;
import com.voting.backend.model.VoterStatus;
import com.voting.backend.repository.VoterRepository;
import com.voting.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    @Transactional
    public String register(RegisterRequest request) {
        if (voterRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email address is already registered");
        }
        if (voterRepository.existsByNationalId(request.getNationalId())) {
            throw new IllegalArgumentException("National ID is already registered");
        }

        String verificationToken = UUID.randomUUID().toString();

        Voter voter = Voter.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nationalId(request.getNationalId())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(Role.VOTER)
                .status(VoterStatus.PENDING)
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .build();

        voterRepository.save(voter);

        emailService.sendVerificationEmail(voter.getEmail(), voter.getFullName(), verificationToken);
        auditLogService.log(voter, "REGISTER", "New voter registered: " + voter.getEmail());

        return "Registration successful. Please check your email to verify your account.";
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Voter voter = voterRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), voter.getPassword())) {
            auditLogService.log(voter, "LOGIN_FAILED", "Failed login attempt for: " + voter.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!voter.isEmailVerified()) {
            throw new IllegalArgumentException("Please verify your email before logging in");
        }

        if (voter.getStatus() == VoterStatus.PENDING) {
            throw new IllegalArgumentException("Your account is pending admin approval");
        }

        if (voter.getStatus() == VoterStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Your account has been deactivated. Please contact admin.");
        }

        String token = jwtUtil.generateToken(voter.getEmail());
        auditLogService.log(voter, "LOGIN", "User logged in: " + voter.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(voter.getId())
                .email(voter.getEmail())
                .fullName(voter.getFullName())
                .role(voter.getRole().name())
                .status(voter.getStatus().name())
                .build();
    }

    @Transactional
    public String verifyEmail(String token) {
        Voter voter = voterRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification token"));

        voter.setEmailVerified(true);
        voter.setEmailVerificationToken(null);
        voterRepository.save(voter);

        auditLogService.log(voter, "EMAIL_VERIFIED", "Email verified for: " + voter.getEmail());
        return "Email verified successfully. Your account is pending admin approval.";
    }

    @Transactional
    public String forgotPassword(String email) {
        Voter voter = voterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));

        String resetToken = UUID.randomUUID().toString();
        voter.setPasswordResetToken(resetToken);
        voter.setPasswordResetExpires(LocalDateTime.now().plusHours(1));
        voterRepository.save(voter);

        emailService.sendPasswordResetEmail(voter.getEmail(), voter.getFullName(), resetToken);
        auditLogService.log(voter, "FORGOT_PASSWORD", "Password reset requested for: " + voter.getEmail());

        return "Password reset instructions have been sent to your email.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        Voter voter = voterRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (voter.getPasswordResetExpires() == null ||
                LocalDateTime.now().isAfter(voter.getPasswordResetExpires())) {
            throw new IllegalArgumentException("Password reset token has expired. Please request a new one.");
        }

        voter.setPassword(passwordEncoder.encode(request.getNewPassword()));
        voter.setPasswordResetToken(null);
        voter.setPasswordResetExpires(null);
        voterRepository.save(voter);

        auditLogService.log(voter, "PASSWORD_RESET", "Password reset completed for: " + voter.getEmail());
        return "Password has been reset successfully. You can now log in.";
    }
}
