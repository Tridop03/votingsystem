package com.voting.backend.service;

import com.voting.backend.dto.request.VoteRequest;
import com.voting.backend.exception.AlreadyVotedException;
import com.voting.backend.exception.ElectionClosedException;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.*;
import com.voting.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VotingService Tests")
class VotingServiceTest {

    @Mock private VoteRepository voteRepository;
    @Mock private VoterRepository voterRepository;
    @Mock private CandidateRepository candidateRepository;
    @Mock private ElectionCategoryRepository categoryRepository;
    @Mock private ElectionRepository electionRepository;
    @Mock private AuditLogService auditLogService;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks
    private VotingService votingService;

    private Voter voter;
    private Election election;
    private ElectionCategory category;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        voter = Voter.builder()
                .id(1L)
                .email("voter@example.com")
                .fullName("Test Voter")
                .status(VoterStatus.ACTIVE)
                .build();

        election = Election.builder()
                .id(10L)
                .title("General Election 2024")
                .isActive(true)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(5))
                .build();

        category = ElectionCategory.builder()
                .id(20L)
                .categoryName("President")
                .election(election)
                .build();

        candidate = Candidate.builder()
                .id(30L)
                .fullName("Alice Smith")
                .party("Party A")
                .electionCategory(category)
                .build();
    }

    // ─── Cast Vote ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Cast vote - success for eligible voter")
    void castVote_success() {
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));
        when(voteRepository.existsByVoterIdAndElectionCategoryId(1L, 20L)).thenReturn(false);
        when(candidateRepository.findById(30L)).thenReturn(Optional.of(candidate));
        when(voteRepository.save(any(Vote.class))).thenAnswer(inv -> {
            Vote v = inv.getArgument(0);
            v.setId(100L);
            return v;
        });

        Vote result = votingService.castVote(request, "voter@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getVoter()).isEqualTo(voter);
        assertThat(result.getCandidate()).isEqualTo(candidate);
        verify(auditLogService).log(eq(voter), eq("VOTE_CAST"), anyString());
        verify(notificationService).createNotification(eq(1L), anyString());
        verify(emailService).sendVoteConfirmationEmail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Cast vote - throws when voter already voted in category")
    void castVote_alreadyVoted_throwsException() {
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));
        when(voteRepository.existsByVoterIdAndElectionCategoryId(1L, 20L)).thenReturn(true);

        assertThatThrownBy(() -> votingService.castVote(request, "voter@example.com"))
                .isInstanceOf(AlreadyVotedException.class)
                .hasMessageContaining("already voted");
    }

    @Test
    @DisplayName("Cast vote - throws when election is not active")
    void castVote_electionNotActive_throwsException() {
        election.setActive(false);
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> votingService.castVote(request, "voter@example.com"))
                .isInstanceOf(ElectionClosedException.class)
                .hasMessageContaining("not active");
    }

    @Test
    @DisplayName("Cast vote - throws when voting period has not started")
    void castVote_votingNotStarted_throwsException() {
        election.setStartTime(LocalDateTime.now().plusHours(2));
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> votingService.castVote(request, "voter@example.com"))
                .isInstanceOf(ElectionClosedException.class)
                .hasMessageContaining("not started");
    }

    @Test
    @DisplayName("Cast vote - throws when voting period has ended")
    void castVote_votingEnded_throwsException() {
        election.setEndTime(LocalDateTime.now().minusHours(1));
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> votingService.castVote(request, "voter@example.com"))
                .isInstanceOf(ElectionClosedException.class)
                .hasMessageContaining("ended");
    }

    @Test
    @DisplayName("Cast vote - throws when candidate does not belong to the category")
    void castVote_candidateCategoryMismatch_throwsException() {
        ElectionCategory otherCategory = ElectionCategory.builder()
                .id(99L).categoryName("Other").election(election).build();
        candidate.setElectionCategory(otherCategory);

        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));
        when(voteRepository.existsByVoterIdAndElectionCategoryId(1L, 20L)).thenReturn(false);
        when(candidateRepository.findById(30L)).thenReturn(Optional.of(candidate));

        assertThatThrownBy(() -> votingService.castVote(request, "voter@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    @DisplayName("Cast vote - throws when voter account is inactive")
    void castVote_inactiveVoter_throwsException() {
        voter.setStatus(VoterStatus.DEACTIVATED);
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));

        assertThatThrownBy(() -> votingService.castVote(request, "voter@example.com"))
                .isInstanceOf(ElectionClosedException.class)
                .hasMessageContaining("not active");
    }

    @Test
    @DisplayName("Cast vote - throws when voter not found")
    void castVote_voterNotFound_throwsException() {
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(voterRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> votingService.castVote(request, "nobody@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
