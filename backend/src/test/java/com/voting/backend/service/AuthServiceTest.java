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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock private VoterRepository voterRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private EmailService emailService;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private Voter activeVoter;
    private Voter pendingVoter;

    @BeforeEach
    void setUp() {
        activeVoter = Voter.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .password("$2a$12$hashedpassword")
                .nationalId("NID-001")
                .role(Role.VOTER)
                .status(VoterStatus.ACTIVE)
                .emailVerified(true)
                .build();

        pendingVoter = Voter.builder()
                .id(2L)
                .fullName("Jane Doe")
                .email("jane@example.com")
                .password("$2a$12$hashedpassword")
                .nationalId("NID-002")
                .role(Role.VOTER)
                .status(VoterStatus.PENDING)
                .emailVerified(true)
                .build();
    }

    // ─── Register Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Register - success when email and national ID are unique")
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("New User");
        request.setEmail("newuser@example.com");
        request.setPassword("Password1");
        request.setNationalId("NID-999");

        when(voterRepository.existsByEmail(anyString())).thenReturn(false);
        when(voterRepository.existsByNationalId(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$encoded");
        when(voterRepository.save(any(Voter.class))).thenReturn(activeVoter);

        String result = authService.register(request);

        assertThat(result).contains("Registration successful");
        verify(emailService).sendVerificationEmail(anyString(), anyString(), anyString());
        verify(voterRepository).save(any(Voter.class));
    }

    @Test
    @DisplayName("Register - throws when email already exists")
    void register_emailAlreadyExists_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setNationalId("NID-NEW");

        when(voterRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email address is already registered");
    }

    @Test
    @DisplayName("Register - throws when national ID already exists")
    void register_nationalIdAlreadyExists_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newemail@example.com");
        request.setNationalId("NID-001");

        when(voterRepository.existsByEmail(anyString())).thenReturn(false);
        when(voterRepository.existsByNationalId("NID-001")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("National ID is already registered");
    }

    // ─── Login Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login - success with valid credentials and active account")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Password1");

        when(voterRepository.findByEmail("john@example.com")).thenReturn(Optional.of(activeVoter));
        when(passwordEncoder.matches("Password1", activeVoter.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyString())).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRole()).isEqualTo("VOTER");
    }

    @Test
    @DisplayName("Login - throws BadCredentials when password is wrong")
    void login_wrongPassword_throwsBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("WrongPassword");

        when(voterRepository.findByEmail("john@example.com")).thenReturn(Optional.of(activeVoter));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Login - throws when email is not verified")
    void login_emailNotVerified_throwsException() {
        activeVoter.setEmailVerified(false);
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Password1");

        when(voterRepository.findByEmail("john@example.com")).thenReturn(Optional.of(activeVoter));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("verify your email");
    }

    @Test
    @DisplayName("Login - throws when account is pending")
    void login_pendingAccount_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jane@example.com");
        request.setPassword("Password1");

        when(voterRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(pendingVoter));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pending admin approval");
    }

    @Test
    @DisplayName("Login - throws when account is deactivated")
    void login_deactivatedAccount_throwsException() {
        activeVoter.setStatus(VoterStatus.DEACTIVATED);
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Password1");

        when(voterRepository.findByEmail("john@example.com")).thenReturn(Optional.of(activeVoter));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("deactivated");
    }

    // ─── Verify Email Tests ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Verify email - success with valid token")
    void verifyEmail_success() {
        activeVoter.setEmailVerified(false);
        activeVoter.setEmailVerificationToken("valid-token-123");

        when(voterRepository.findByEmailVerificationToken("valid-token-123"))
                .thenReturn(Optional.of(activeVoter));
        when(voterRepository.save(any(Voter.class))).thenReturn(activeVoter);

        String result = authService.verifyEmail("valid-token-123");

        assertThat(result).contains("Email verified successfully");
        assertThat(activeVoter.isEmailVerified()).isTrue();
        assertThat(activeVoter.getEmailVerificationToken()).isNull();
    }

    @Test
    @DisplayName("Verify email - throws when token is invalid")
    void verifyEmail_invalidToken_throwsException() {
        when(voterRepository.findByEmailVerificationToken("bad-token"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyEmail("bad-token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid or expired verification token");
    }

    // ─── Forgot Password Tests ───────────────────────────────────────────────────

    @Test
    @DisplayName("Forgot password - sends reset email for existing account")
    void forgotPassword_success() {
        when(voterRepository.findByEmail("john@example.com")).thenReturn(Optional.of(activeVoter));
        when(voterRepository.save(any(Voter.class))).thenReturn(activeVoter);

        String result = authService.forgotPassword("john@example.com");

        assertThat(result).contains("Password reset instructions");
        verify(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Forgot password - throws when email not found")
    void forgotPassword_emailNotFound_throwsException() {
        when(voterRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.forgotPassword("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── Reset Password Tests ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Reset password - success with valid non-expired token")
    void resetPassword_success() {
        activeVoter.setPasswordResetToken("reset-token");
        activeVoter.setPasswordResetExpires(LocalDateTime.now().plusHours(1));

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("reset-token");
        request.setNewPassword("NewPassword1");

        when(voterRepository.findByPasswordResetToken("reset-token")).thenReturn(Optional.of(activeVoter));
        when(passwordEncoder.encode("NewPassword1")).thenReturn("$2a$12$newencoded");
        when(voterRepository.save(any(Voter.class))).thenReturn(activeVoter);

        String result = authService.resetPassword(request);

        assertThat(result).contains("Password has been reset");
        assertThat(activeVoter.getPasswordResetToken()).isNull();
    }

    @Test
    @DisplayName("Reset password - throws when token is expired")
    void resetPassword_expiredToken_throwsException() {
        activeVoter.setPasswordResetToken("expired-token");
        activeVoter.setPasswordResetExpires(LocalDateTime.now().minusHours(1));

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("expired-token");
        request.setNewPassword("NewPassword1");

        when(voterRepository.findByPasswordResetToken("expired-token"))
                .thenReturn(Optional.of(activeVoter));

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expired");
    }
}
