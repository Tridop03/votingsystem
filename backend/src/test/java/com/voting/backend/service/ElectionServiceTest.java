package com.voting.backend.service;

import com.voting.backend.dto.request.ElectionRequest;
import com.voting.backend.dto.response.ElectionResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Election;
import com.voting.backend.model.Role;
import com.voting.backend.model.Voter;
import com.voting.backend.model.VoterStatus;
import com.voting.backend.repository.ElectionCategoryRepository;
import com.voting.backend.repository.ElectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ElectionService Tests")
class ElectionServiceTest {

    @Mock private ElectionRepository electionRepository;
    @Mock private ElectionCategoryRepository categoryRepository;
    @Mock private AuditLogService auditLogService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private ElectionService electionService;

    private Voter admin;
    private Election election;
    private ElectionRequest validRequest;

    @BeforeEach
    void setUp() {
        admin = Voter.builder()
                .id(1L).fullName("Admin User").email("admin@example.com")
                .role(Role.ADMIN).status(VoterStatus.ACTIVE).build();

        election = Election.builder()
                .id(10L).title("Test Election")
                .description("Test Description")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .isActive(false).resultsLocked(false)
                .createdBy(admin)
                .categories(new ArrayList<>())
                .build();

        validRequest = new ElectionRequest();
        validRequest.setTitle("New Election");
        validRequest.setDescription("Description");
        validRequest.setStartTime(LocalDateTime.now().plusDays(1));
        validRequest.setEndTime(LocalDateTime.now().plusDays(7));
        validRequest.setCategories(List.of("President", "Governor"));
    }

    // ─── Create Election ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Create election - success with valid data")
    void createElection_success() {
        when(electionRepository.save(any(Election.class))).thenReturn(election);
        when(electionRepository.findById(election.getId())).thenReturn(Optional.of(election));

        ElectionResponse response = electionService.createElection(validRequest, admin);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Election");
        verify(categoryRepository, times(2)).save(any());
        verify(auditLogService).log(eq(admin), eq("ELECTION_CREATED"), anyString());
    }

    @Test
    @DisplayName("Create election - throws when end time is before start time")
    void createElection_invalidDates_throwsException() {
        validRequest.setEndTime(LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> electionService.createElection(validRequest, admin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End time must be after start time");
    }

    // ─── Update Election ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Update election - success")
    void updateElection_success() {
        when(electionRepository.findById(10L)).thenReturn(Optional.of(election));
        when(electionRepository.save(any(Election.class))).thenReturn(election);

        ElectionResponse response = electionService.updateElection(10L, validRequest, admin);

        assertThat(response).isNotNull();
        verify(auditLogService).log(eq(admin), eq("ELECTION_UPDATED"), anyString());
    }

    @Test
    @DisplayName("Update election - throws when election not found")
    void updateElection_notFound_throwsException() {
        when(electionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> electionService.updateElection(999L, validRequest, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── Delete Election ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Delete election - success")
    void deleteElection_success() {
        when(electionRepository.findById(10L)).thenReturn(Optional.of(election));

        electionService.deleteElection(10L, admin);

        verify(electionRepository).delete(election);
        verify(auditLogService).log(eq(admin), eq("ELECTION_DELETED"), anyString());
    }

    // ─── Publish Election ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Publish election - sets isActive to true and notifies voters")
    void publishElection_success() {
        when(electionRepository.findById(10L)).thenReturn(Optional.of(election));
        when(electionRepository.save(any(Election.class))).thenReturn(election);

        ElectionResponse response = electionService.publishElection(10L, admin);

        assertThat(election.isActive()).isTrue();
        verify(notificationService).createNotificationForAllVoters(anyString());
        verify(auditLogService).log(eq(admin), eq("ELECTION_PUBLISHED"), anyString());
    }

    // ─── Lock Results ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Lock results - sets resultsLocked to true")
    void lockResults_success() {
        when(electionRepository.findById(10L)).thenReturn(Optional.of(election));
        when(electionRepository.save(any(Election.class))).thenReturn(election);

        electionService.lockResults(10L, admin);

        assertThat(election.isResultsLocked()).isTrue();
        verify(auditLogService).log(eq(admin), eq("RESULTS_LOCKED"), anyString());
    }

    // ─── Get Elections ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Get all elections - returns all")
    void getAllElections_returnsAll() {
        when(electionRepository.findAll()).thenReturn(List.of(election));

        List<ElectionResponse> result = electionService.getAllElections();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Get election by ID - throws when not found")
    void getElectionById_notFound_throwsException() {
        when(electionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> electionService.getElectionById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
