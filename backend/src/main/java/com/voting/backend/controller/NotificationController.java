package com.voting.backend.controller;

import com.voting.backend.dto.response.ApiResponse;
import com.voting.backend.model.Notification;
import com.voting.backend.service.NotificationService;
import com.voting.backend.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final VoterService voterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        var voter = voterService.getVoterByEmail(userDetails.getUsername());
        List<Notification> notifications = notificationService.getNotificationsForVoter(voter.getId());
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        var voter = voterService.getVoterByEmail(userDetails.getUsername());
        long count = notificationService.getUnreadCount(voter.getId());
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved",
                Map.of("unreadCount", count)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var voter = voterService.getVoterByEmail(userDetails.getUsername());
        Notification notification = notificationService.markAsRead(id, voter.getId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }
}
