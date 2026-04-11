package com.voting.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateResponse {
    private Long id;
    private String fullName;
    private String party;
    private String bio;
    private String photoUrl;
    private Long electionCategoryId;
    private String categoryName;
    private Long electionId;
}
