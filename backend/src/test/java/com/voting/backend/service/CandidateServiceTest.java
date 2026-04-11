package com.voting.backend.service;

import com.voting.backend.dto.request.CandidateRequest;
import com.voting.backend.dto.response.CandidateResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Candidate;
import com.voting.backend.model.*;
import com.voting.backend.repository.CandidateRepository;
import com.voting.backend.repository.ElectionCategoryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CandidateService Tests")
class CandidateServiceTest {

    @Mock private CandidateRepository candidateRepository;
    @Mock private ElectionCategoryRepository categoryRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private CandidateService candidateService;

    private Voter admin;
    private Election election;
    private ElectionCategory category;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        admin = Voter.builder().id(1L).email("admin@example.com").role(Role.ADMIN).build();

        election = Election.builder().id(10L).title("Test Election")
                .createdBy(admin).categories(new ArrayList<>()).build();

        category = ElectionCategory.builder()
                .id(20L).categoryName("President").election(election)
                .candidates(new ArrayList<>()).build();

        candidate = Candidate.builder()
                .id(30L).fullName("Alice Smith").party("Party A")
                .bio("Bio text").photoUrl("http://photo.url")
                .electionCategory(category).build();
    }

    @Test
    @DisplayName("Create candidate - success")
    void createCandidate_success() {
        CandidateRequest request = new CandidateRequest();
        request.setFullName("Bob Jones");
        request.setParty("Party B");
        request.setBio("Some bio");
        request.setElectionCategoryId(20L);

        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        CandidateResponse result = candidateService.createCandidate(request, admin);

        assertThat(result).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo("President");
        verify(auditLogService).log(eq(admin), eq("CANDIDATE_CREATED"), anyString());
    }

    @Test
    @DisplayName("Create candidate - throws when category not found")
    void createCandidate_categoryNotFound_throwsException() {
        CandidateRequest request = new CandidateRequest();
        request.setFullName("Bob");
        request.setElectionCategoryId(999L);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.createCandidate(request, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Update candidate - success")
    void updateCandidate_success() {
        CandidateRequest request = new CandidateRequest();
        request.setFullName("Updated Name");
        request.setParty("New Party");

        when(candidateRepository.findById(30L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        CandidateResponse result = candidateService.updateCandidate(30L, request, admin);

        assertThat(candidate.getFullName()).isEqualTo("Updated Name");
        assertThat(candidate.getParty()).isEqualTo("New Party");
        verify(auditLogService).log(eq(admin), eq("CANDIDATE_UPDATED"), anyString());
    }

    @Test
    @DisplayName("Update candidate - throws when not found")
    void updateCandidate_notFound_throwsException() {
        CandidateRequest request = new CandidateRequest();
        when(candidateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.updateCandidate(999L, request, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Delete candidate - success")
    void deleteCandidate_success() {
        when(candidateRepository.findById(30L)).thenReturn(Optional.of(candidate));

        candidateService.deleteCandidate(30L, admin);

        verify(candidateRepository).delete(candidate);
        verify(auditLogService).log(eq(admin), eq("CANDIDATE_DELETED"), anyString());
    }

    @Test
    @DisplayName("Delete candidate - throws when not found")
    void deleteCandidate_notFound_throwsException() {
        when(candidateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.deleteCandidate(999L, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get all candidates - returns full list")
    void getAllCandidates_returnsList() {
        when(candidateRepository.findAll()).thenReturn(List.of(candidate));

        List<CandidateResponse> result = candidateService.getAllCandidates();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Alice Smith");
    }
}

