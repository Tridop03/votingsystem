package com.voting.backend.controller;

import com.voting.backend.dto.request.VoteRequest;
import com.voting.backend.dto.response.ApiResponse;
import com.voting.backend.model.Vote;
import com.voting.backend.service.VotingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VotingService votingService;

    @PostMapping("/cast")
    public ResponseEntity<ApiResponse<Map<String, Object>>> castVote(
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Vote vote = votingService.castVote(request, userDetails.getUsername());
        Map<String, Object> result = Map.of(
                "voteId", vote.getId(),
                "candidateId", vote.getCandidate().getId(),
                "candidateName", vote.getCandidate().getFullName(),
                "electionCategoryId", vote.getElectionCategory().getId(),
                "categoryName", vote.getElectionCategory().getCategoryName(),
                "votedAt", vote.getVotedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vote cast successfully", result));
    }

    @GetMapping("/status/{electionId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVotingStatus(
            @PathVariable Long electionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> status = votingService.getVotingStatus(electionId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Voting status retrieved", status));
    }
}