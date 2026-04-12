package com.voting.backend.service;

import com.voting.backend.dto.request.CandidateRequest;
import com.voting.backend.dto.response.CandidateResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Candidate;
import com.voting.backend.model.ElectionCategory;
import com.voting.backend.model.Voter;
import com.voting.backend.repository.CandidateRepository;
import com.voting.backend.repository.ElectionCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionCategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public CandidateResponse createCandidate(CandidateRequest request, Voter admin) {
        ElectionCategory category = categoryRepository.findById(request.getElectionCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Election category", request.getElectionCategoryId()));

        Candidate candidate = Candidate.builder()
                .fullName(request.getFullName())
                .party(request.getParty())
                .bio(request.getBio())
                .photoUrl(request.getPhotoUrl())
                .electionCategory(category)
                .build();

        candidate = candidateRepository.save(candidate);
        auditLogService.log(admin, "CANDIDATE_CREATED",
                "Candidate created: " + candidate.getFullName() + " in category: " + category.getCategoryName());
        return toCandidateResponse(candidate);
    }

    @Transactional
    public CandidateResponse updateCandidate(Long id, CandidateRequest request, Voter admin) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", id));

        if (request.getFullName() != null) candidate.setFullName(request.getFullName());
        if (request.getParty() != null) candidate.setParty(request.getParty());
        if (request.getBio() != null) candidate.setBio(request.getBio());
        if (request.getPhotoUrl() != null) candidate.setPhotoUrl(request.getPhotoUrl());

        if (request.getElectionCategoryId() != null &&
                !request.getElectionCategoryId().equals(candidate.getElectionCategory().getId())) {
            ElectionCategory category = categoryRepository.findById(request.getElectionCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Election category", request.getElectionCategoryId()));
            candidate.setElectionCategory(category);
        }

        candidateRepository.save(candidate);
        auditLogService.log(admin, "CANDIDATE_UPDATED", "Candidate updated: " + candidate.getFullName());
        return toCandidateResponse(candidate);
    }

    @Transactional
    public void deleteCandidate(Long id, Voter admin) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", id));
        auditLogService.log(admin, "CANDIDATE_DELETED", "Candidate deleted: " + candidate.getFullName());
        candidateRepository.delete(candidate);
    }

    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(this::toCandidateResponse)
                .collect(Collectors.toList());
    }

    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", id));
        return toCandidateResponse(candidate);
    }

    public CandidateResponse toCandidateResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .fullName(candidate.getFullName())
                .party(candidate.getParty())
                .bio(candidate.getBio())
                .photoUrl(candidate.getPhotoUrl())
                .electionCategoryId(candidate.getElectionCategory().getId())
                .categoryName(candidate.getElectionCategory().getCategoryName())
                .electionId(candidate.getElectionCategory().getElection().getId())
                .build();
    }
}
