package com.voting.backend.service;

import com.voting.backend.model.AuditLog;
import com.voting.backend.model.Voter;
import com.voting.backend.repository.AuditLogRepository;
import com.voting.backend.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final VoterRepository voterRepository;

    @Transactional
    public void log(Long userId, String action, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .details(details)
                    .build();

            if (userId != null) {
                voterRepository.findById(userId).ifPresent(auditLog::setUser);
            }

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: action={}, details={}, error={}", action, details, e.getMessage());
        }
    }

    @Transactional
    public void log(Voter voter, String action, String details) {
        log(voter != null ? voter.getId() : null, action, details);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsByUserId(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
