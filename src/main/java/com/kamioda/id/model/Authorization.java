package com.kamioda.id.model;

import java.time.LocalDateTime;

import com.kamioda.id.component.HashUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "authorizations",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_auth_id", columnNames = "AuthID")
    }
)
public class Authorization {
    private static final long EXPIRED_MIN = 3;
    @Id
    @Column(name = "AuthID", nullable = false)
    @Pattern(regexp = "auth[01]-[0-9]{8}")
    private String authID;
    @Column(name = "AppID", nullable = false)
    @Pattern(regexp = "app-[0-9a-f]{12}4[0-9a-f]{3}[89ab][0-9a-f]{15}$", message = "AppID must be in the format app-{uuid v4}")
    private String appId;
    @Column(name = "RedirectURI", nullable = false)
    private String redirectURI;
    @Column(name = "AuthorizedID", nullable = true)
    @Pattern(regexp = "^\\d{13}$", message = "AuthorizedID must be a 13-digit number")
    private String authorizedId;
    @Column(name = "CodeChallenge", nullable = false)
    @Size(min = 8, max = 128)
    private String codeChallenge;
    @Column(name = "CodeChallengeMethod", nullable = false)
    private String codeChallengeMethod;
    @Column(name = "ReferenceTime", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime referenceTime;
    public Authorization() {}
    private boolean expired() {
        return referenceTime != null && referenceTime.isBefore(LocalDateTime.now().minusMinutes(EXPIRED_MIN));
    }
    public boolean verify(String redirectUri, String codeVerifier) {
        if (expired() || !redirectURI.equals(redirectUri)) return false;
        if ("plain".equalsIgnoreCase(codeChallengeMethod)) return codeVerifier.equals(codeChallenge);
        return HashUtils.toHash(codeVerifier, codeChallengeMethod, "base64").equals(codeChallenge);
    }
    public String getMasterID() {
        return authorizedId;
    }
    public String getAppID() {
        return appId;
    }
    public String getRedirectUri(String authCode) {
        return redirectURI + "?auth_code=" + authCode;
    }
}
