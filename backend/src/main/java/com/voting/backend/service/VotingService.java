package com.voting.backend.service;

import com.voting.backend.dto.request.VoteRequest;
import com.voting.backend.exception.AlreadyVotedException;
import com.voting.backend.exception.ElectionClosedException;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.*;
import com.voting.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VotingService {

    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionCategoryRepository categoryRepository;
    private final ElectionRepository electionRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public Vote castVote(VoteRequest request, String voterEmail) {
        Voter voter = voterRepository.findByEmail(voterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));

        if (voter.getStatus() != VoterStatus.ACTIVE) {
            throw new ElectionClosedException("Your account is not active. Cannot cast vote.");
        }

        ElectionCategory category = categoryRepository.findById(request.getElectionCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Election category", request.getElectionCategoryId()));

        Election election = category.getElection();
        LocalDateTime now = LocalDateTime.now();

        if (!election.isActive()) {
            throw new ElectionClosedException("This election is not active");
        }
        if (now.isBefore(election.getStartTime())) {
            throw new ElectionClosedException("Voting has not started yet. Starts at: " + election.getStartTime());
        }
        if (now.isAfter(election.getEndTime())) {
            throw new ElectionClosedException("Voting has ended for this election");
        }

        if (voteRepository.existsByVoterIdAndElectionCategoryId(voter.getId(), category.getId())) {
            throw new AlreadyVotedException("You have already voted in the category: " + category.getCategoryName());
        }

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", request.getCandidateId()));

        if (!candidate.getElectionCategory().getId().equals(category.getId())) {
            throw new IllegalArgumentException("Candidate does not belong to the specified election category");
        }

        Vote vote = Vote.builder()
                .voter(voter)
                .candidate(candidate)
                .electionCategory(category)
                .build();

        vote = voteRepository.save(vote);

        auditLogService.log(voter, "VOTE_CAST",
                String.format("Vote cast in election '%s', category '%s'",
                        election.getTitle(), category.getCategoryName()));

        notificationService.createNotification(voter.getId(),
                "Your vote has been recorded in '" + election.getTitle() +
                        "' for category '" + category.getCategoryName() + "'");

        emailService.sendVoteConfirmationEmail(
                voter.getEmail(), voter.getFullName(),
                election.getTitle(), category.getCategoryName(), candidate.getFullName()
        );

        return vote;
    }

    public Map<String, Object> getVotingStatus(Long electionId, String voterEmail) {
        Voter voter = voterRepository.findByEmail(voterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election", electionId));

        List<Vote> votes = voteRepository.findByVoterIdAndElectionId(voter.getId(), electionId);
        List<ElectionCategory> categories = categoryRepository.findByElectionId(electionId);

        Map<String, Object> status = new HashMap<>();
        status.put("electionId", electionId);
        status.put("electionTitle", election.getTitle());
        status.put("totalCategories", categories.size());
        status.put("votedCategories", votes.size());
        status.put("hasVotedInAllCategories", votes.size() == categories.size());

        Map<Long, Boolean> categoryVotedMap = new HashMap<>();
        categories.forEach(cat ->
                categoryVotedMap.put(cat.getId(),
                        votes.stream().anyMatch(v -> v.getElectionCategory().getId().equals(cat.getId())))
        );
        status.put("categoryVotingStatus", categoryVotedMap);

        return status;
    }
}

