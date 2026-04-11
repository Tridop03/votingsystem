package com.voting.backend.service;

import com.voting.backend.dto.request.UpdateProfileRequest;
import com.voting.backend.dto.response.VoterResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Vote;
import com.voting.backend.model.Voter;
import com.voting.backend.repository.VoteRepository;
import com.voting.backend.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoterService {

    private final VoterRepository voterRepository;
    private final VoteRepository voteRepository;
    private final AuditLogService auditLogService;

    public Voter getVoterByEmail(String email) {
        return voterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found with email: " + email));
    }

    public VoterResponse getProfile(String email) {
        Voter voter = getVoterByEmail(email);
        return toVoterResponse(voter);
    }

    @Transactional
    public VoterResponse updateProfile(String email, UpdateProfileRequest request) {
        Voter voter = getVoterByEmail(email);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            voter.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            voter.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            voter.setAddress(request.getAddress());
        }

        voterRepository.save(voter);
        auditLogService.log(voter, "PROFILE_UPDATED", "Profile updated for: " + voter.getEmail());
        return toVoterResponse(voter);
    }

    @Transactional
    public VoterResponse updateProfilePhoto(String email, String photoUrl) {
        Voter voter = getVoterByEmail(email);
        voter.setProfilePicture(photoUrl);
        voterRepository.save(voter);
        auditLogService.log(voter, "PROFILE_PHOTO_UPDATED", "Profile photo updated for: " + voter.getEmail());
        return toVoterResponse(voter);
    }

    @Transactional
    public String deleteAccount(String email) {
        Voter voter = getVoterByEmail(email);
        auditLogService.log(voter, "ACCOUNT_DELETED", "Account deleted for: " + voter.getEmail());
        voterRepository.delete(voter);
        return "Account deleted successfully";
    }

    public List<Vote> getVotingHistory(String email) {
        Voter voter = getVoterByEmail(email);
        return voteRepository.findByVoterId(voter.getId());
    }

    public static VoterResponse toVoterResponse(Voter voter) {
        return VoterResponse.builder()
                .id(voter.getId())
                .fullName(voter.getFullName())
                .email(voter.getEmail())
                .nationalId(voter.getNationalId())
                .phone(voter.getPhone())
                .address(voter.getAddress())
                .profilePicture(voter.getProfilePicture())
                .role(voter.getRole().name())
                .status(voter.getStatus().name())
                .emailVerified(voter.isEmailVerified())
                .createdAt(voter.getCreatedAt())
                .build();
    }
}