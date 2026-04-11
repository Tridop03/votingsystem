package com.voting.backend.service;


import com.voting.backend.dto.response.VoterResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Role;
import com.voting.backend.model.Voter;
import com.voting.backend.model.VoterStatus;
import com.voting.backend.repository.VoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Tests")
class AdminServiceTest {

    @Mock private VoterRepository voterRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuditLogService auditLogService;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks
    private AdminService adminService;

    private Voter admin;
    private Voter pendingVoter;
    private Voter activeVoter;

    @BeforeEach
    void setUp() {
        admin = Voter.builder()
                .id(1L).email("admin@example.com").fullName("Admin")
                .role(Role.ADMIN).status(VoterStatus.ACTIVE).build();

        pendingVoter = Voter.builder()
                .id(2L).email("pending@example.com").fullName("Pending User")
                .nationalId("NID-P").role(Role.VOTER).status(VoterStatus.PENDING).build();

        activeVoter = Voter.builder()
                .id(3L).email("active@example.com").fullName("Active User")
                .nationalId("NID-A").role(Role.VOTER).status(VoterStatus.ACTIVE).build();
    }

    @Test
    @DisplayName("Get all voters - returns full list")
    void getAllVoters_returnsAll() {
        when(voterRepository.findAll()).thenReturn(List.of(admin, pendingVoter, activeVoter));

        List<VoterResponse> result = adminService.getAllVoters();

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Get pending voters - returns only pending")
    void getPendingVoters_returnsOnlyPending() {
        when(voterRepository.findByStatus(VoterStatus.PENDING)).thenReturn(List.of(pendingVoter));

        List<VoterResponse> result = adminService.getPendingVoters();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Get voter by ID - returns voter")
    void getVoterById_success() {
        when(voterRepository.findById(2L)).thenReturn(Optional.of(pendingVoter));

        VoterResponse result = adminService.getVoterById(2L);

        assertThat(result.getEmail()).isEqualTo("pending@example.com");
    }

    @Test
    @DisplayName("Get voter by ID - throws when not found")
    void getVoterById_notFound_throwsException() {
        when(voterRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getVoterById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Approve voter - sets status to ACTIVE and sends email")
    void approveVoter_success() {
        when(voterRepository.findById(2L)).thenReturn(Optional.of(pendingVoter));
        when(voterRepository.save(any(Voter.class))).thenReturn(pendingVoter);

        VoterResponse result = adminService.approveVoter(2L, admin);

        assertThat(pendingVoter.getStatus()).isEqualTo(VoterStatus.ACTIVE);
        verify(emailService).sendAccountApprovedEmail(anyString(), anyString());
        verify(notificationService).createNotification(eq(2L), anyString());
        verify(auditLogService).log(eq(admin), eq("VOTER_APPROVED"), anyString());
    }

    @Test
    @DisplayName("Approve voter - throws when voter is already active")
    void approveVoter_alreadyActive_throwsException() {
        when(voterRepository.findById(3L)).thenReturn(Optional.of(activeVoter));

        assertThatThrownBy(() -> adminService.approveVoter(3L, admin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already active");
    }

    @Test
    @DisplayName("Deactivate voter - sets status to DEACTIVATED and sends email")
    void deactivateVoter_success() {
        when(voterRepository.findById(3L)).thenReturn(Optional.of(activeVoter));
        when(voterRepository.save(any(Voter.class))).thenReturn(activeVoter);

        VoterResponse result = adminService.deactivateVoter(3L, admin);

        assertThat(activeVoter.getStatus()).isEqualTo(VoterStatus.DEACTIVATED);
        verify(emailService).sendAccountDeactivatedEmail(anyString(), anyString());
        verify(auditLogService).log(eq(admin), eq("VOTER_DEACTIVATED"), anyString());
    }

    @Test
    @DisplayName("Deactivate voter - throws when voter is already deactivated")
    void deactivateVoter_alreadyDeactivated_throwsException() {
        activeVoter.setStatus(VoterStatus.DEACTIVATED);
        when(voterRepository.findById(3L)).thenReturn(Optional.of(activeVoter));

        assertThatThrownBy(() -> adminService.deactivateVoter(3L, admin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already deactivated");
    }

    @Test
    @DisplayName("Reset voter password - generates temp password and saves")
    void resetVoterPassword_success() {
        when(voterRepository.findById(3L)).thenReturn(Optional.of(activeVoter));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$tempencoded");
        when(voterRepository.save(any(Voter.class))).thenReturn(activeVoter);

        String result = adminService.resetVoterPassword(3L, admin);

        assertThat(result).contains("Password reset successfully");
        assertThat(result).contains("Temporary password:");
        verify(auditLogService).log(eq(admin), eq("VOTER_PASSWORD_RESET"), anyString());
    }
}

