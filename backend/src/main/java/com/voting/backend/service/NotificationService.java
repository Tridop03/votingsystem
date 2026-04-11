package com.voting.backend.service;

import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.exception.UnauthorizedException;
import com.voting.backend.model.Notification;
import com.voting.backend.model.Voter;
import com.voting.backend.repository.NotificationRepository;
import com.voting.backend.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final VoterRepository voterRepository;

    @Transactional
    public void createNotification(Long voterId, String message) {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", voterId));
        Notification notification = Notification.builder()
                .voter(voter)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForAllVoters(String message) {
        voterRepository.findAll().forEach(voter ->
                notificationRepository.save(Notification.builder()
                        .voter(voter)
                        .message(message)
                        .isRead(false)
                        .build())
        );
    }

    public List<Notification> getNotificationsForVoter(Long voterId) {
        return notificationRepository.findByVoterIdOrderByCreatedAtDesc(voterId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId, Long voterId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        if (!notification.getVoter().getId().equals(voterId)) {
            throw new UnauthorizedException("You are not authorized to update this notification");
        }
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public long getUnreadCount(Long voterId) {
        return notificationRepository.countByVoterIdAndIsReadFalse(voterId);
    }
}

