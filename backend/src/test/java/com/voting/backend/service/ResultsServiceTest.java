package com.voting.backend.service;

import com.voting.backend.dto.response.ResultsResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.*;
import com.voting.backend.repository.ElectionRepository;
import com.voting.backend.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResultsService Tests")
class ResultsServiceTest {

    @Mock private ElectionRepository electionRepository;
    @Mock private VoteRepository voteRepository;

    @InjectMocks
    private ResultsService resultsService;

    private Voter admin;
    private Election election;
    private ElectionCategory category;
    private Candidate candidateA;
    private Candidate candidateB;

    @BeforeEach
    void setUp() {
        admin = Voter.builder().id(1L).email("admin@example.com").build();

        candidateA = Candidate.builder().id(10L).fullName("Alice").party("Party A").build();
        candidateB = Candidate.builder().id(11L).fullName("Bob").party("Party B").build();

        List<Candidate> candidates = new ArrayList<>(List.of(candidateA, candidateB));

        category = ElectionCategory.builder()
                .id(20L).categoryName("President")
                .candidates(candidates)
                .build();
        candidateA.setElectionCategory(category);
        candidateB.setElectionCategory(category);

        election = Election.builder()
                .id(100L).title("General Election 2024")
                .isActive(true).resultsLocked(false)
                .createdBy(admin)
                .categories(new ArrayList<>(List.of(category)))
                .build();
        category.setElection(election);
    }

    @Test
    @DisplayName("Get results - returns correct vote counts and winner")
    void getResults_returnsCorrectData() {
        when(electionRepository.findById(100L)).thenReturn(Optional.of(election));
        when(voteRepository.countDistinctVotersByElectionId(100L)).thenReturn(3L);
        when(voteRepository.countByCategoryIdAndCandidateId(20L, 10L)).thenReturn(2L); // Alice: 2
        when(voteRepository.countByCategoryIdAndCandidateId(20L, 11L)).thenReturn(1L); // Bob: 1

        ResultsResponse response = resultsService.getResults(100L);

        assertThat(response.getElectionId()).isEqualTo(100L);
        assertThat(response.getElectionTitle()).isEqualTo("General Election 2024");
        assertThat(response.getTotalVotersTurnout()).isEqualTo(3L);
        assertThat(response.getCategoryResults()).hasSize(1);

        ResultsResponse.CategoryResult catResult = response.getCategoryResults().get(0);
        assertThat(catResult.getCategoryName()).isEqualTo("President");
        assertThat(catResult.getTotalVotesInCategory()).isEqualTo(3L);
        assertThat(catResult.getCandidates()).hasSize(2);

        // Winner is Alice (2 votes) — first in sorted list
        ResultsResponse.CandidateResult winner = catResult.getCandidates().get(0);
        assertThat(winner.getCandidateName()).isEqualTo("Alice");
        assertThat(winner.getVoteCount()).isEqualTo(2L);
        assertThat(winner.isWinner()).isTrue();
        assertThat(winner.getPercentage()).isEqualTo(66.67);

        // Bob is second
        ResultsResponse.CandidateResult loser = catResult.getCandidates().get(1);
        assertThat(loser.getCandidateName()).isEqualTo("Bob");
        assertThat(loser.isWinner()).isFalse();
    }

    @Test
    @DisplayName("Get results - handles zero votes gracefully")
    void getResults_zeroVotes_handledGracefully() {
        when(electionRepository.findById(100L)).thenReturn(Optional.of(election));
        when(voteRepository.countDistinctVotersByElectionId(100L)).thenReturn(0L);
        when(voteRepository.countByCategoryIdAndCandidateId(anyLong(), anyLong())).thenReturn(0L);

        ResultsResponse response = resultsService.getResults(100L);

        ResultsResponse.CategoryResult catResult = response.getCategoryResults().get(0);
        assertThat(catResult.getTotalVotesInCategory()).isEqualTo(0L);
        catResult.getCandidates().forEach(c -> {
            assertThat(c.getVoteCount()).isZero();
            assertThat(c.getPercentage()).isZero();
            assertThat(c.isWinner()).isFalse();
        });
    }

    @Test
    @DisplayName("Get results - throws when election not found")
    void getResults_electionNotFound_throwsException() {
        when(electionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultsService.getResults(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get results - marks correct winner in tie (first candidate wins tie)")
    void getResults_tie_firstCandidateWins() {
        when(electionRepository.findById(100L)).thenReturn(Optional.of(election));
        when(voteRepository.countDistinctVotersByElectionId(100L)).thenReturn(2L);
        when(voteRepository.countByCategoryIdAndCandidateId(anyLong(), anyLong())).thenReturn(1L);

        ResultsResponse response = resultsService.getResults(100L);
        ResultsResponse.CategoryResult catResult = response.getCategoryResults().get(0);

        long winnerCount = catResult.getCandidates().stream()
                .filter(ResultsResponse.CandidateResult::isWinner).count();
        assertThat(winnerCount).isEqualTo(1L);
    }
}

