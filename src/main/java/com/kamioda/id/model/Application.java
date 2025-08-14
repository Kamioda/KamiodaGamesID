package com.kamioda.id.model;

import java.sql.Types;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.validator.constraints.URL;

import com.kamioda.id.component.HashUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(
    name = "applications",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_app_id", columnNames = "AppID")
    }
)
public class Application {
    @Id
    @Column(name = "AppID", nullable = false, unique = true)
    @Pattern(regexp = "app-[0-9a-f]{12}4[0-9a-f]{3}[89ab][0-9a-f]{15}$", message = "AppID must be in the format app-{uuid v4}")
    private String appId;
    @Column(name = "AppSecret", nullable = false)
    @Pattern(regexp = "^[0-9A-F]{128}$", message = "App Secret must be a 128-character hexadecimal string")
    private String appSecretHashString;
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "AppName", nullable = false)
    private String appName;
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "AppDescription", nullable = true)
    private String appDescription;
    @Column(name = "RedirectUri", nullable = false)
    @URL(message = "Invalid Redirect URI")
    private String redirectUri;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "DeveloperID", 
        referencedColumnName = "ID", 
        nullable = false, 
        insertable = false, 
        updatable = false,
        foreignKey = @ForeignKey(name = "FK_application_developer")
    )
    private User developer;
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Token> tokens;
    public Application() {}
    public Application(String appId, String appSecretHashString, String appName, String appDescription, String redirectUri) {
        this.appId = appId;
        this.appSecretHashString = appSecretHashString;
        this.appName = appName;
        this.appDescription = appDescription;
        this.redirectUri = redirectUri;
    }
    public boolean equalRedirectUri(String redirectUri) {
        return this.redirectUri.equals(redirectUri);
    }
    public static String hashAppSecret(String appSecret) {
        return HashUtils.toHash(appSecret, "sha512", "hex", 9);
    }
    public boolean matchAppSecret(String appSecret) {
        return this.appSecretHashString.equals(hashAppSecret(appSecret));
    }
    public boolean matchDeveloper(String developerId) {
        return this.developer.getId().equals(developerId);
    }
    public String getAppId() {
        return appId;
    }
}
