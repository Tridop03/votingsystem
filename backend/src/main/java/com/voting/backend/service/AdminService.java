package com.voting.backend.service;

import com.voting.backend.dto.response.VoterResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.AuditLog;
import com.voting.backend.model.Voter;
import com.voting.backend.model.VoterStatus;
import com.voting.backend.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public List<VoterResponse> getAllVoters() {
        return voterRepository.findAll().stream()
                .map(VoterService::toVoterResponse)
                .collect(Collectors.toList());
    }

    public List<VoterResponse> getPendingVoters() {
        return voterRepository.findByStatus(VoterStatus.PENDING).stream()
                .map(VoterService::toVoterResponse)
                .collect(Collectors.toList());
    }

    public VoterResponse getVoterById(Long id) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", id));
        return VoterService.toVoterResponse(voter);
    }

    public List<AuditLog> getVoterActivity(Long id) {
        voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", id));
        return auditLogService.getLogsByUserId(id);
    }

    @Transactional
    public VoterResponse approveVoter(Long id, Voter admin) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", id));

        if (voter.getStatus() == VoterStatus.ACTIVE) {
            throw new IllegalArgumentException("Voter account is already active");
        }

        voter.setStatus(VoterStatus.ACTIVE);
        voterRepository.save(voter);

        emailService.sendAccountApprovedEmail(voter.getEmail(), voter.getFullName());
        notificationService.createNotification(voter.getId(),
                "Your account has been approved. You can now participate in elections.");
        auditLogService.log(admin, "VOTER_APPROVED",
                "Voter approved: " + voter.getEmail() + " by admin: " + admin.getEmail());

        return VoterService.toVoterResponse(voter);
    }

    @Transactional
    public VoterResponse deactivateVoter(Long id, Voter admin) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", id));

        if (voter.getStatus() == VoterStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Voter account is already deactivated");
        }

        voter.setStatus(VoterStatus.DEACTIVATED);
        voterRepository.save(voter);

        emailService.sendAccountDeactivatedEmail(voter.getEmail(), voter.getFullName());
        auditLogService.log(admin, "VOTER_DEACTIVATED",
                "Voter deactivated: " + voter.getEmail() + " by admin: " + admin.getEmail());

        return VoterService.toVoterResponse(voter);
    }

    @Transactional
    public String resetVoterPassword(Long id, Voter admin) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", id));

        // Generate a temporary password
        String tempPassword = "Temp@" + System.currentTimeMillis() % 100000;
        voter.setPassword(passwordEncoder.encode(tempPassword));
        voterRepository.save(voter);

        auditLogService.log(admin, "VOTER_PASSWORD_RESET",
                "Password reset by admin for voter: " + voter.getEmail());

        return "Password reset successfully. Temporary password: " + tempPassword;
    }
}