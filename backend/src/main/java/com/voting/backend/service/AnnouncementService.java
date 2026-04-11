package com.voting.backend.service;

import com.voting.backend.dto.request.AnnouncementRequest;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Announcement;
import com.voting.backend.model.Voter;
import com.voting.backend.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Announcement createAnnouncement(AnnouncementRequest request, Voter admin) {
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .createdBy(admin)
                .build();

        announcement = announcementRepository.save(announcement);

        notificationService.createNotificationForAllVoters(
                "New announcement: " + request.getTitle() + " - " + request.getMessage()
        );

        auditLogService.log(admin, "ANNOUNCEMENT_CREATED", "Announcement created: " + request.getTitle());
        return announcement;
    }

    @Transactional
    public Announcement updateAnnouncement(Long id, AnnouncementRequest request, Voter admin) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));

        announcement.setTitle(request.getTitle());
        announcement.setMessage(request.getMessage());

        announcementRepository.save(announcement);
        auditLogService.log(admin, "ANNOUNCEMENT_UPDATED", "Announcement updated: " + request.getTitle());
        return announcement;
    }

    @Transactional
    public void deleteAnnouncement(Long id, Voter admin) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));
        auditLogService.log(admin, "ANNOUNCEMENT_DELETED", "Announcement deleted: " + announcement.getTitle());
        announcementRepository.delete(announcement);
    }
}
