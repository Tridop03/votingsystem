package com.voting.backend.repository;

import com.voting.backend.model.Voter;
import com.voting.backend.model.VoterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<Voter, Long> {
    Optional<Voter> findByEmail(String email);
    Optional<Voter> findByEmailVerificationToken(String token);
    Optional<Voter> findByPasswordResetToken(String token);
    boolean existsByEmail(String email);
    boolean existsByNationalId(String nationalId);
    List<Voter> findByStatus(VoterStatus status);
}
