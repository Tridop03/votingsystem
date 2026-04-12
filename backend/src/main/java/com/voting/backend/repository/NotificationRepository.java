package com.voting.backend.repository;

import com.voting.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByVoterIdOrderByCreatedAtDesc(Long voterId);
    List<Notification> findByVoterIdAndIsReadFalse(Long voterId);
    long countByVoterIdAndIsReadFalse(Long voterId);
}
