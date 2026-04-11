package com.voting.backend.repository;

import com.voting.backend.model.ElectionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionCategoryRepository extends JpaRepository<ElectionCategory, Long> {
    List<ElectionCategory> findByElectionId(Long electionId);
}

