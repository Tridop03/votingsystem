package com.voting.backend.controller;

import com.voting.backend.dto.request.CandidateRequest;
import com.voting.backend.dto.response.ApiResponse;
import com.voting.backend.dto.response.CandidateResponse;
import com.voting.backend.service.CandidateService;
import com.voting.backend.service.VoterService;
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
@RequestMapping("/api/admin/candidates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CandidateController {

    private final CandidateService candidateService;
    private final VoterService voterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getAllCandidates() {
        List<CandidateResponse> candidates = candidateService.getAllCandidates();
        return ResponseEntity.ok(ApiResponse.success("Candidates retrieved successfully", candidates));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateResponse>> createCandidate(
            @Valid @RequestBody CandidateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        CandidateResponse candidate = candidateService.createCandidate(request, admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Candidate created successfully", candidate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateCandidate(
            @PathVariable Long id,
            @Valid @RequestBody CandidateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        CandidateResponse candidate = candidateService.updateCandidate(id, request, admin);
        return ResponseEntity.ok(ApiResponse.success("Candidate updated successfully", candidate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCandidate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        candidateService.deleteCandidate(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Candidate deleted successfully"));
    }
}

