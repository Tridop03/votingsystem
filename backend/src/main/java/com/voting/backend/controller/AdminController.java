package com.voting.backend.controller;

import com.voting.backend.dto.request.ElectionRequest;
import com.voting.backend.dto.response.*;
import com.voting.backend.model.AuditLog;
import com.voting.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ElectionService electionService;
    private final VoterService voterService;

    // ─── Voter Management ───────────────────────────────────────────────────────

    @GetMapping("/voters")
    public ResponseEntity<ApiResponse<List<VoterResponse>>> getAllVoters() {
        List<VoterResponse> voters = adminService.getAllVoters();
        return ResponseEntity.ok(ApiResponse.success("Voters retrieved successfully", voters));
    }

    @GetMapping("/voters/pending")
    public ResponseEntity<ApiResponse<List<VoterResponse>>> getPendingVoters() {
        List<VoterResponse> voters = adminService.getPendingVoters();
        return ResponseEntity.ok(ApiResponse.success("Pending voters retrieved", voters));
    }

    @GetMapping("/voters/{id}")
    public ResponseEntity<ApiResponse<VoterResponse>> getVoterById(@PathVariable Long id) {
        VoterResponse voter = adminService.getVoterById(id);
        return ResponseEntity.ok(ApiResponse.success("Voter retrieved successfully", voter));
    }

    @GetMapping("/voters/{id}/activity")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getVoterActivity(@PathVariable Long id) {
        List<AuditLog> activity = adminService.getVoterActivity(id);
        return ResponseEntity.ok(ApiResponse.success("Voter activity retrieved", activity));
    }

    @PutMapping("/voters/{id}/approve")
    public ResponseEntity<ApiResponse<VoterResponse>> approveVoter(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        VoterResponse voter = adminService.approveVoter(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Voter approved successfully", voter));
    }

    @PutMapping("/voters/{id}/deactivate")
    public ResponseEntity<ApiResponse<VoterResponse>> deactivateVoter(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        VoterResponse voter = adminService.deactivateVoter(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Voter deactivated successfully", voter));
    }

    @PutMapping("/voters/{id}/reset-password")
    public ResponseEntity<ApiResponse<String>> resetVoterPassword(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        String result = adminService.resetVoterPassword(id, admin);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ─── Election Management ──

    @GetMapping("/elections")
    public ResponseEntity<ApiResponse<List<ElectionResponse>>> getAllElections() {
        List<ElectionResponse> elections = electionService.getAllElections();
        return ResponseEntity.ok(ApiResponse.success("Elections retrieved successfully", elections));
    }

    @PostMapping("/elections")
    public ResponseEntity<ApiResponse<ElectionResponse>> createElection(
            @Valid @RequestBody ElectionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        ElectionResponse election = electionService.createElection(request, admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Election created successfully", election));
    }

    @PutMapping("/elections/{id}")
    public ResponseEntity<ApiResponse<ElectionResponse>> updateElection(
            @PathVariable Long id,
            @Valid @RequestBody ElectionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        ElectionResponse election = electionService.updateElection(id, request, admin);
        return ResponseEntity.ok(ApiResponse.success("Election updated successfully", election));
    }

    @DeleteMapping("/elections/{id}")
    public ResponseEntity<ApiResponse<String>> deleteElection(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        electionService.deleteElection(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Election deleted successfully"));
    }

    @PutMapping("/elections/{id}/publish")
    public ResponseEntity<ApiResponse<ElectionResponse>> publishElection(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        ElectionResponse election = electionService.publishElection(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Election published successfully", election));
    }

    @PutMapping("/elections/{id}/lock-results")
    public ResponseEntity<ApiResponse<ElectionResponse>> lockResults(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        ElectionResponse election = electionService.lockResults(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Election results locked successfully", election));
    }
}
