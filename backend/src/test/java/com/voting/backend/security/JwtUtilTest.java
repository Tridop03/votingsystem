package com.voting.backend.security;

import com.voting.backend.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    @Mock private JwtConfig jwtConfig;

    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String SECRET = "myTestSecretKeyThatIsAtLeast256BitsLongForHmacSha256Algorithm!";
    private static final long EXPIRATION = 86400000L; // 24 hours
    private static final String EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        lenient().when(jwtConfig.getSecret()).thenReturn(SECRET);
        lenient().when(jwtConfig.getExpiration()).thenReturn(EXPIRATION);
    }

    @Test
    @DisplayName("Generate token - returns non-null token string")
    void generateToken_returnsToken() {
        String token = jwtUtil.generateToken(EMAIL);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("Extract email - returns correct email from token")
    void extractEmail_returnsCorrectEmail() {
        String token = jwtUtil.generateToken(EMAIL);
        String extracted = jwtUtil.extractEmail(token);

        assertThat(extracted).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Token is valid - returns true for fresh token and matching user")
    void validateToken_validToken_returnsTrue() {
        String token = jwtUtil.generateToken(EMAIL);
        UserDetails userDetails = new User(EMAIL, "password", Collections.emptyList());

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Token is valid - returns false when email does not match")
    void validateToken_wrongEmail_returnsFalse() {
        String token = jwtUtil.generateToken(EMAIL);
        UserDetails userDetails = new User("other@example.com", "password", Collections.emptyList());

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - returns false for garbage string")
    void isTokenValid_invalidString_returnsFalse() {
        boolean isValid = jwtUtil.isTokenValid("this.is.garbage");

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - returns false for null-ish empty input")
    void isTokenValid_emptyString_returnsFalse() {
        assertThat(jwtUtil.isTokenValid("")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - returns true for freshly generated token")
    void isTokenValid_freshToken_returnsTrue() {
        String token = jwtUtil.generateToken(EMAIL);

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("Two tokens for same email are different (timestamp-based)")
    void generateToken_twoTokens_areDifferent() throws InterruptedException {
        String token1 = jwtUtil.generateToken(EMAIL);
        Thread.sleep(1100); // Wait > 1s for different iat (issuedAt is in seconds)
        String token2 = jwtUtil.generateToken(EMAIL);

        // Both should be valid but different due to iat claim
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtUtil.extractEmail(token1)).isEqualTo(jwtUtil.extractEmail(token2));
    }
}

