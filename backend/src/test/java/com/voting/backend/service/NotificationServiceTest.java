package com.voting.backend.service;

import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.exception.UnauthorizedException;
import com.voting.backend.model.Notification;
import com.voting.backend.model.Voter;
import com.voting.backend.repository.NotificationRepository;
import com.voting.backend.repository.VoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private VoterRepository voterRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Voter voter;
    private Notification notification;

    @BeforeEach
    void setUp() {
        voter = Voter.builder().id(1L).email("voter@example.com").fullName("Test Voter").build();

        notification = Notification.builder()
                .id(10L).voter(voter).message("Test notification").isRead(false).build();
    }

    @Test
    @DisplayName("Create notification - saves notification for valid voter")
    void createNotification_success() {
        when(voterRepository.findById(1L)).thenReturn(Optional.of(voter));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.createNotification(1L, "Test notification");

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Create notification - throws when voter not found")
    void createNotification_voterNotFound_throwsException() {
        when(voterRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.createNotification(999L, "msg"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get notifications - returns list for voter")
    void getNotificationsForVoter_returnsList() {
        when(notificationRepository.findByVoterIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification));

        List<Notification> result = notificationService.getNotificationsForVoter(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("Test notification");
    }

    @Test
    @DisplayName("Mark as read - sets isRead to true for owner")
    void markAsRead_success() {
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.markAsRead(10L, 1L);

        assertThat(result.isRead()).isTrue();
    }

    @Test
    @DisplayName("Mark as read - throws UnauthorizedException when voter does not own notification")
    void markAsRead_wrongVoter_throwsException() {
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(10L, 999L))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Mark as read - throws when notification not found")
    void markAsRead_notFound_throwsException() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get unread count - returns correct count")
    void getUnreadCount_returnsCount() {
        when(notificationRepository.countByVoterIdAndIsReadFalse(1L)).thenReturn(5L);

        long count = notificationService.getUnreadCount(1L);

        assertThat(count).isEqualTo(5L);
    }
}

