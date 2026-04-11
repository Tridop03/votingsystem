package com.voting.backend.controller;

import com.voting.backend.dto.request.AnnouncementRequest;
import com.voting.backend.dto.response.ApiResponse;
import com.voting.backend.model.Announcement;
import com.voting.backend.service.AnnouncementService;
import com.voting.backend.service.VoterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/announcements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final VoterService voterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Announcement>>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved", announcements));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(
            @Valid @RequestBody AnnouncementRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        Announcement announcement = announcementService.createAnnouncement(request, admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Announcement created successfully", announcement));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Announcement>> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        Announcement announcement = announcementService.updateAnnouncement(id, request, admin);
        return ResponseEntity.ok(ApiResponse.success("Announcement updated successfully", announcement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAnnouncement(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var admin = voterService.getVoterByEmail(userDetails.getUsername());
        announcementService.deleteAnnouncement(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully"));
    }
}
