package com.voting.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CandidateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String party;
    private String bio;
    private String photoUrl;

    @NotNull(message = "Election category ID is required")
    private Long electionCategoryId;
}
