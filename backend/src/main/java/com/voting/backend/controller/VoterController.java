package com.voting.backend.controller;

import com.voting.backend.dto.request.UpdateProfileRequest;
import com.voting.backend.dto.response.ApiResponse;
import com.voting.backend.dto.response.VoterResponse;
import com.voting.backend.model.Notification;
import com.voting.backend.model.Vote;
import com.voting.backend.service.NotificationService;
import com.voting.backend.service.VoterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voter")
@RequiredArgsConstructor
public class VoterController {

    private final VoterService voterService;
    private final NotificationService notificationService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<VoterResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        VoterResponse profile = voterService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<VoterResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        VoterResponse updated = voterService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PutMapping("/profile/photo")
    public ResponseEntity<ApiResponse<VoterResponse>> updateProfilePhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String photoUrl) {
        VoterResponse updated = voterService.updateProfilePhoto(userDetails.getUsername(), photoUrl);
        return ResponseEntity.ok(ApiResponse.success("Profile photo updated successfully", updated));
    }

    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<String>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        String message = voterService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Vote>>> getVotingHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Vote> history = voterService.getVotingHistory(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Voting history retrieved", history));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        var voter = voterService.getVoterByEmail(userDetails.getUsername());
        List<Notification> notifications = notificationService.getNotificationsForVoter(voter.getId());
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markNotificationRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var voter = voterService.getVoterByEmail(userDetails.getUsername());
        Notification notification = notificationService.markAsRead(id, voter.getId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }
}
