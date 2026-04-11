package com.voting.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoterResponse {
    private Long id;
    private String fullName;
    private String email;
    private String nationalId;
    private String phone;
    private String address;
    private String profilePicture;
    private String role;
    private String status;
    private boolean emailVerified;
    private LocalDateTime createdAt;
}

