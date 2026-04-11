package com.voting.backend.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultsResponse {
    private Long electionId;
    private String electionTitle;
    private long totalVotersTurnout;
    private boolean resultsLocked;
    private List<CategoryResult> categoryResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResult {
        private Long categoryId;
        private String categoryName;
        private long totalVotesInCategory;
        private List<CandidateResult> candidates;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateResult {
        private Long candidateId;
        private String candidateName;
        private String party;
        private String photoUrl;
        private long voteCount;
        private double percentage;
        private boolean isWinner;
    }
}