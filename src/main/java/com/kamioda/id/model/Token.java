package com.kamioda.id.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.kamioda.id.component.HashUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "tokens",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_token_access", columnNames = "AccessToken"),
        @UniqueConstraint(name = "UK_token_refresh", columnNames = "RefreshToken")
    }
)
public class Token {
    public static final long AccessTokenExpiresSec = 3600; // 1 hour
    public static final long RefreshTokenExpiresSec = 604800; // 7 days
    @Id
    @Column(name = "AccessToken", unique = true, nullable = false, length = 128)
    @Size(min = 128, max = 128)
    @Pattern(regexp = "^[0-9A-F]{128}$", message = "AccessToken must be a SHA-512 hash (128 hexadecimal characters)")
    private String accessToken;
    @Column(name = "RefreshToken", unique = true, nullable = false, length = 128)
    @Size(min = 128, max = 128)
    @Pattern(regexp = "^[0-9A-F]{128}$", message = "RefreshToken must be a SHA-512 hash (128 hexadecimal characters)")
    private String refreshToken;
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(
        name = "ID", 
        referencedColumnName = "ID", 
        nullable = false, 
        foreignKey = @ForeignKey(name = "FK_token_user")
    )
    private User user;
    @ManyToOne
    @JoinColumn(
        name = "AppID", 
        referencedColumnName = "AppID", 
        foreignKey = @ForeignKey(name = "FK_token_application")
    )
    private Application application;
    public Token() {}
    public Token(String accessToken, String refreshToken, User user, Application application) {
        this.accessToken = hashAccessToken(accessToken);
        this.refreshToken = hashRefreshToken(refreshToken);
        this.user = user;
        this.application = application;
    }
    public boolean accessTokenExpired() {
        return createdAt.plusSeconds(AccessTokenExpiresSec).isBefore(LocalDateTime.now());
    }
    public boolean refreshTokenExpired() {
        return createdAt.plusSeconds(RefreshTokenExpiresSec).isBefore(LocalDateTime.now());
    }
    public String getUserMasterID() {
        return user.getId();
    }
    public Application getApplication() {
        return application;
    }
    public User getUser() {
        return user;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public static String hashAccessToken(String accessToken) {
        return HashUtils.toHash(accessToken, "sha512", "hex", 7);
    }
    public static String hashRefreshToken(String refreshToken) {
        return HashUtils.toHash(refreshToken, "sha512", "hex", 4);
    }
}
