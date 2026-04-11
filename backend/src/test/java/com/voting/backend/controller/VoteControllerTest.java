package com.voting.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.voting.backend.dto.request.VoteRequest;
import com.voting.backend.exception.AlreadyVotedException;
import com.voting.backend.exception.ElectionClosedException;
import com.voting.backend.model.*;
import com.voting.backend.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.voting.backend.security.JwtUtil;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Import;
import com.voting.backend.config.SecurityConfig;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(VoteController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("VoteController Integration Tests")
class VoteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean  private VotingService votingService;
    @MockitoBean  private JwtUtil jwtUtil;
    @MockitoBean  private UserDetailsService userDetailsService;

    private Vote mockVote;

    @BeforeEach
    void setUp() {
        Voter voter = Voter.builder().id(1L).email("voter@example.com").build();
        Election election = Election.builder().id(10L).title("General Election").build();
        ElectionCategory category = ElectionCategory.builder()
                .id(20L).categoryName("President").election(election).build();
        Candidate candidate = Candidate.builder()
                .id(30L).fullName("Alice Smith").electionCategory(category).build();

        mockVote = Vote.builder()
                .id(100L).voter(voter).candidate(candidate)
                .electionCategory(category).votedAt(LocalDateTime.now()).build();
    }

    @Test
    @WithMockUser(username = "voter@example.com", roles = "VOTER")
    @DisplayName("POST /api/votes/cast - 201 on successful vote")
    void castVote_success_returns201() throws Exception {
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(votingService.castVote(any(VoteRequest.class), eq("voter@example.com")))
                .thenReturn(mockVote);

        mockMvc.perform(post("/api/votes/cast")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Vote cast successfully"));
    }

    @Test
    @WithMockUser(username = "voter@example.com", roles = "VOTER")
    @DisplayName("POST /api/votes/cast - 409 when already voted")
    void castVote_alreadyVoted_returns409() throws Exception {
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(votingService.castVote(any(), any()))
                .thenThrow(new AlreadyVotedException("You have already voted in this category"));

        mockMvc.perform(post("/api/votes/cast")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "voter@example.com", roles = "VOTER")
    @DisplayName("POST /api/votes/cast - 400 when election is closed")
    void castVote_electionClosed_returns400() throws Exception {
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        when(votingService.castVote(any(), any()))
                .thenThrow(new ElectionClosedException("Voting has ended for this election"));

        mockMvc.perform(post("/api/votes/cast")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "voter@example.com", roles = "VOTER")
    @DisplayName("POST /api/votes/cast - 400 when request body is missing required fields")
    void castVote_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/votes/cast")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/votes/cast - 403 when unauthenticated")
    void castVote_unauthenticated_returns403() throws Exception {
        VoteRequest request = new VoteRequest();
        request.setCandidateId(30L);
        request.setElectionCategoryId(20L);

        mockMvc.perform(post("/api/votes/cast")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "voter@example.com", roles = "VOTER")
    @DisplayName("GET /api/votes/status/{electionId} - 200 with voting status")
    void getVotingStatus_returns200() throws Exception {
        when(votingService.getVotingStatus(eq(10L), eq("voter@example.com")))
                .thenReturn(Map.of(
                        "electionId", 10L,
                        "totalCategories", 2,
                        "votedCategories", 1,
                        "hasVotedInAllCategories", false
                ));

        mockMvc.perform(get("/api/votes/status/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.electionId").value(10));
    }
}
