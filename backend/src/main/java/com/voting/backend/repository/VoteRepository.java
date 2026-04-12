package com.voting.backend.repository;

import com.voting.backend.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVoterIdAndElectionCategoryId(Long voterId, Long electionCategoryId);

    Optional<Vote> findByVoterIdAndElectionCategoryId(Long voterId, Long electionCategoryId);

    List<Vote> findByVoterId(Long voterId);

    List<Vote> findByElectionCategoryId(Long electionCategoryId);

    @Query("SELECT v FROM Vote v WHERE v.electionCategory.election.id = :electionId")
    List<Vote> findByElectionId(@Param("electionId") Long electionId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.electionCategory.id = :categoryId AND v.candidate.id = :candidateId")
    long countByCategoryIdAndCandidateId(@Param("categoryId") Long categoryId, @Param("candidateId") Long candidateId);

    @Query("SELECT COUNT(DISTINCT v.voter.id) FROM Vote v WHERE v.electionCategory.election.id = :electionId")
    long countDistinctVotersByElectionId(@Param("electionId") Long electionId);

    @Query("SELECT v FROM Vote v WHERE v.voter.id = :voterId AND v.electionCategory.election.id = :electionId")
    List<Vote> findByVoterIdAndElectionId(@Param("voterId") Long voterId, @Param("electionId") Long electionId);
}
