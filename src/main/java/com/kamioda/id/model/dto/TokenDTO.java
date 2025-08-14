package com.kamioda.id.model.dto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TokenDTO {
    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("refresh_token")
    private final String refreshToken;

    private final LocalDateTime createdAt;

    // 秒数は "expires_in"
    @JsonProperty("expires_in")
    private final long expiresIn = 180L * 60L;

    @JsonProperty("refresh_token_expires_in")
    private final long refreshTokenExpiresIn = 10080L * 60L;
    
    @JsonProperty("expires_at")
    private final LocalDateTime expiresAt;

    @JsonProperty("refresh_token_expires_at")
    private final LocalDateTime refreshTokenExpiresAt;

    @JsonCreator
    public TokenDTO(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            LocalDateTime createdAt
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.expiresAt = createdAt.plusSeconds(expiresIn);
        this.refreshTokenExpiresAt = createdAt.plusSeconds(refreshTokenExpiresIn);
    }

    // ---- getters ----
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getExpiresIn() { return expiresIn; }
    public long getRefreshTokenExpiresIn() { return refreshTokenExpiresIn; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getRefreshTokenExpiresAt() { return refreshTokenExpiresAt; }
}
