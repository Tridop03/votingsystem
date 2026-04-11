package com.voting.backend.service;

import com.voting.backend.dto.request.ElectionRequest;
import com.voting.backend.dto.response.CandidateResponse;
import com.voting.backend.dto.response.ElectionResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Election;
import com.voting.backend.model.ElectionCategory;
import com.voting.backend.model.Voter;
import com.voting.backend.repository.ElectionCategoryRepository;
import com.voting.backend.repository.ElectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final ElectionCategoryRepository categoryRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @Transactional
    public ElectionResponse createElection(ElectionRequest request, Voter admin) {
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Election election = Election.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(false)
                .resultsLocked(false)
                .createdBy(admin)
                .build();

        election = electionRepository.save(election);

        if (request.getCategories() != null) {
            for (String categoryName : request.getCategories()) {
                ElectionCategory category = ElectionCategory.builder()
                        .election(election)
                        .categoryName(categoryName)
                        .build();
                categoryRepository.save(category);
            }
        }

        auditLogService.log(admin, "ELECTION_CREATED", "Election created: " + election.getTitle());
        return toElectionResponse(electionRepository.findById(election.getId()).orElseThrow());
    }

    @Transactional
    public ElectionResponse updateElection(Long id, ElectionRequest request, Voter admin) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", id));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setStartTime(request.getStartTime());
        election.setEndTime(request.getEndTime());

        electionRepository.save(election);
        auditLogService.log(admin, "ELECTION_UPDATED", "Election updated: " + election.getTitle());
        return toElectionResponse(election);
    }

    @Transactional
    public void deleteElection(Long id, Voter admin) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", id));
        auditLogService.log(admin, "ELECTION_DELETED", "Election deleted: " + election.getTitle());
        electionRepository.delete(election);
    }

    @Transactional
    public ElectionResponse publishElection(Long id, Voter admin) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", id));
        election.setActive(true);
        electionRepository.save(election);

        notificationService.createNotificationForAllVoters(
                "New election published: " + election.getTitle() +
                        ". Voting runs from " + election.getStartTime() + " to " + election.getEndTime()
        );

        auditLogService.log(admin, "ELECTION_PUBLISHED", "Election published: " + election.getTitle());
        return toElectionResponse(election);
    }

    @Transactional
    public ElectionResponse lockResults(Long id, Voter admin) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", id));
        election.setResultsLocked(true);
        electionRepository.save(election);
        auditLogService.log(admin, "RESULTS_LOCKED", "Results locked for election: " + election.getTitle());
        return toElectionResponse(election);
    }

    public List<ElectionResponse> getAllElections() {
        return electionRepository.findAll().stream()
                .map(this::toElectionResponse)
                .collect(Collectors.toList());
    }

    public List<ElectionResponse> getActiveElections() {
        return electionRepository.findCurrentlyActiveElections(LocalDateTime.now()).stream()
                .map(this::toElectionResponse)
                .collect(Collectors.toList());
    }

    public List<ElectionResponse> getUpcomingElections() {
        return electionRepository.findUpcomingElections(LocalDateTime.now()).stream()
                .map(this::toElectionResponse)
                .collect(Collectors.toList());
    }

    public List<ElectionResponse> getPastElections() {
        return electionRepository.findPastElections(LocalDateTime.now()).stream()
                .map(this::toElectionResponse)
                .collect(Collectors.toList());
    }

    public ElectionResponse getElectionById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", id));
        return toElectionResponse(election);
    }

    public ElectionResponse toElectionResponse(Election election) {
        List<ElectionResponse.CategoryResponse> categoryResponses = new ArrayList<>();

        if (election.getCategories() != null) {
            for (ElectionCategory cat : election.getCategories()) {
                List<CandidateResponse> candidateResponses = new ArrayList<>();
                if (cat.getCandidates() != null) {
                    for (var c : cat.getCandidates()) {
                        candidateResponses.add(CandidateResponse.builder()
                                .id(c.getId())
                                .fullName(c.getFullName())
                                .party(c.getParty())
                                .bio(c.getBio())
                                .photoUrl(c.getPhotoUrl())
                                .electionCategoryId(cat.getId())
                                .categoryName(cat.getCategoryName())
                                .electionId(election.getId())
                                .build());
                    }
                }
                categoryResponses.add(ElectionResponse.CategoryResponse.builder()
                        .id(cat.getId())
                        .categoryName(cat.getCategoryName())
                        .candidates(candidateResponses)
                        .build());
            }
        }

        return ElectionResponse.builder()
                .id(election.getId())
                .title(election.getTitle())
                .description(election.getDescription())
                .startTime(election.getStartTime())
                .endTime(election.getEndTime())
                .isActive(election.isActive())
                .resultsLocked(election.isResultsLocked())
                .createdById(election.getCreatedBy().getId())
                .createdByName(election.getCreatedBy().getFullName())
                .createdAt(election.getCreatedAt())
                .categories(categoryResponses)
                .build();
    }
}
