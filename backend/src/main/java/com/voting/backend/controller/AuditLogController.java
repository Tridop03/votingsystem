package com.voting.backend.controller;

import com.voting.backend.dto.response.ApiResponse;
import com.voting.backend.model.AuditLog;
import com.voting.backend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAllAuditLogs() {
        List<AuditLog> logs = auditLogService.getAllLogs();
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", logs));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogsByUser(@PathVariable Long userId) {
        List<AuditLog> logs = auditLogService.getLogsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("User audit logs retrieved", logs));
    }
}

