package com.voting.backend.repository;

import com.voting.backend.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByElectionCategoryId(Long electionCategoryId);
    List<Candidate> findByElectionCategoryElectionId(Long electionId);
}
