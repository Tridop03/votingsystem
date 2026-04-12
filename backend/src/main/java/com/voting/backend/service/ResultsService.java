package com.voting.backend.service;

import com.voting.backend.dto.response.ResultsResponse;
import com.voting.backend.exception.ResourceNotFoundException;
import com.voting.backend.model.Candidate;
import com.voting.backend.model.Election;
import com.voting.backend.model.ElectionCategory;
import com.voting.backend.repository.ElectionRepository;
import com.voting.backend.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultsService {

    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public ResultsResponse getResults(Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election", electionId));

        long totalTurnout = voteRepository.countDistinctVotersByElectionId(electionId);
        List<ResultsResponse.CategoryResult> categoryResults = new ArrayList<>();

        for (ElectionCategory category : election.getCategories()) {
            List<ResultsResponse.CandidateResult> candidateResults = new ArrayList<>();
            long totalVotesInCategory = 0;

            // Count votes per candidate
            for (Candidate candidate : category.getCandidates()) {
                long count = voteRepository.countByCategoryIdAndCandidateId(category.getId(), candidate.getId());
                totalVotesInCategory += count;
                candidateResults.add(ResultsResponse.CandidateResult.builder()
                        .candidateId(candidate.getId())
                        .candidateName(candidate.getFullName())
                        .party(candidate.getParty())
                        .photoUrl(candidate.getPhotoUrl())
                        .voteCount(count)
                        .percentage(0.0) // calculated below after total is known
                        .isWinner(false)  // set after sorting
                        .build());
            }

            // Calculate percentages
            final long total = totalVotesInCategory;
            candidateResults.forEach(r -> {
                double pct = total > 0 ? (r.getVoteCount() * 100.0 / total) : 0.0;
                r.setPercentage(Math.round(pct * 100.0) / 100.0);
            });

            // Mark winner (highest votes, first one wins ties)
            if (!candidateResults.isEmpty()) {
                long maxVotes = candidateResults.stream()
                        .mapToLong(ResultsResponse.CandidateResult::getVoteCount)
                        .max().orElse(0);
                if (maxVotes > 0) {
                    candidateResults.stream()
                            .filter(r -> r.getVoteCount() == maxVotes)
                            .findFirst()
                            .ifPresent(r -> r.setWinner(true));
                }
            }

            // Sort by vote count descending
            candidateResults.sort((a, b) -> Long.compare(b.getVoteCount(), a.getVoteCount()));

            categoryResults.add(ResultsResponse.CategoryResult.builder()
                    .categoryId(category.getId())
                    .categoryName(category.getCategoryName())
                    .totalVotesInCategory(total)
                    .candidates(candidateResults)
                    .build());
        }

        return ResultsResponse.builder()
                .electionId(election.getId())
                .electionTitle(election.getTitle())
                .totalVotersTurnout(totalTurnout)
                .resultsLocked(election.isResultsLocked())
                .categoryResults(categoryResults)
                .build();
    }
}
