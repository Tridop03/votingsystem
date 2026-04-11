package com.voting.backend.controller;

import com.voting.backend.dto.response.ApiResponse;
import com.voting.backend.dto.response.ElectionResponse;
import com.voting.backend.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ElectionResponse>>> getAllElections() {
        List<ElectionResponse> elections = electionService.getAllElections();
        return ResponseEntity.ok(ApiResponse.success("Elections retrieved successfully", elections));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ElectionResponse>>> getActiveElections() {
        List<ElectionResponse> elections = electionService.getActiveElections();
        return ResponseEntity.ok(ApiResponse.success("Active elections retrieved", elections));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<ElectionResponse>>> getUpcomingElections() {
        List<ElectionResponse> elections = electionService.getUpcomingElections();
        return ResponseEntity.ok(ApiResponse.success("Upcoming elections retrieved", elections));
    }

    @GetMapping("/past")
    public ResponseEntity<ApiResponse<List<ElectionResponse>>> getPastElections() {
        List<ElectionResponse> elections = electionService.getPastElections();
        return ResponseEntity.ok(ApiResponse.success("Past elections retrieved", elections));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ElectionResponse>> getElectionById(@PathVariable Long id) {
        ElectionResponse election = electionService.getElectionById(id);
        return ResponseEntity.ok(ApiResponse.success("Election retrieved successfully", election));
    }
}

