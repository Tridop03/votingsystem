package com.voting.backend.repository;

import com.voting.backend.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    List<Election> findByIsActiveTrue();

    @Query("SELECT e FROM Election e WHERE e.startTime > :now ORDER BY e.startTime ASC")
    List<Election> findUpcomingElections(LocalDateTime now);

    @Query("SELECT e FROM Election e WHERE e.endTime < :now ORDER BY e.endTime DESC")
    List<Election> findPastElections(LocalDateTime now);

    @Query("SELECT e FROM Election e WHERE e.isActive = true AND e.startTime <= :now AND e.endTime >= :now")
    List<Election> findCurrentlyActiveElections(LocalDateTime now);
}

