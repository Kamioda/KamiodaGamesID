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
    @Column(name = "RedirectURI", nullable = false)
    private String redirectURI;
    @Column(name = "CodeChallenge", nullable = false)
    @Size(min = 8, max = 128)
    private String codeChallenge;
    @Column(name = "CodeChallengeMethod", nullable = false)
    private String codeChallengeMethod;
    @CreationTimestamp
    @Column(name = "ReferenceTime", nullable = false)
    private LocalDateTime referenceTime;
    @ManyToOne
    @JoinColumn(
        name = "AuthorizedID",
        referencedColumnName = "ID",
        nullable = true,
        foreignKey = @ForeignKey(name = "FK_authorization_user")
    )
    private User authorizedUser;
    @ManyToOne
    @JoinColumn(
        name = "AppID",
        referencedColumnName = "AppID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_authorization_application")
    )
    private Application application;
    public Authorization() {}
    public Authorization(String authID, String redirectURI, String codeChallenge, String codeChallengeMethod, Application application) {
        this.authID = "auth0-" + authID;
        this.redirectURI = redirectURI;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
        this.authorizedUser = null;
        this.application = application;
    }
    public Authorization(Authorization authorizationWithAuthId, String authCode, User user) {
        this.authID = "auth1-" + authCode;
        this.redirectURI = authorizationWithAuthId.redirectURI;
        this.codeChallenge = authorizationWithAuthId.codeChallenge;
        this.codeChallengeMethod = authorizationWithAuthId.codeChallengeMethod;
        this.authorizedUser = user;
        this.application = authorizationWithAuthId.application;
    }
    private boolean expired() {
        return referenceTime != null && referenceTime.isBefore(LocalDateTime.now().minusMinutes(EXPIRED_MIN));
    }
    public boolean verify(String redirectUri, String codeVerifier) {
        if (expired() || !redirectURI.equals(redirectUri)) return false;
        if ("plain".equalsIgnoreCase(codeChallengeMethod)) return codeVerifier.equals(codeChallenge);
        return HashUtils.toHash(codeVerifier, codeChallengeMethod, "base64").equals(codeChallenge);
    }
    public String getMasterID() {
        return authorizedUser != null ? authorizedUser.getId() : null;
    }
    public Application getApp() {
        return application;
    }
    public String getRedirectUri(String authCode) {
        return redirectURI + "?auth_code=" + authCode;
    }
}
