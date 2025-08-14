package com.kamioda.id.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import com.kamioda.id.component.HashUtils;
import com.kamioda.id.model.dto.AccountInformation;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_master_id", columnNames = "ID"),
        @UniqueConstraint(name = "UK_user_id", columnNames = "UserID"),
        @UniqueConstraint(name = "UK_user_email", columnNames = "Email")
    }
)
public class User {
    private static final String PasswordSalt = "YA2FMSnL4QGU2sjO";
    @Id
    @Column(name = "ID", nullable = false, length = 13)
    @Pattern(regexp = "^\\d{13}$", message = "ID must be a 13-digit number")
    @Size(min = 13, max = 13, message = "ID must be 13 digits long")
    private String id;
    @Column(name = "UserID", nullable = false, unique = true, length = 16)
    @NotNull(message = "User ID cannot be null")
    @Size(min = 8, max = 16, message = "User ID must be between 8 and 16 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_]{7,15}$", message = "User ID must be 8-16 characters long, start with a letter or number, and can contain letters, numbers, and underscores")
    private String userId;
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "UserName", nullable = false, unique = false)
    @NotNull(message = "Username cannot be null")
    private String userName;
    @Column(name = "Email", nullable = false, unique = true)
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be a valid email address")
    private String email;
    @Column(name = "Password", nullable = false)
    @NotNull(message = "Password cannot be null")
    @Pattern(regexp = "^[0-9A-F]{128}$", message = "Password must be a 128-character hexadecimal string")
    private String password;
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "UpdatedAt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Token> tokens;
    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Application> applications;
    public User() {}
    public User(String id, String userId, String userName, String email, String password) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = hashPassword(password);
    }
    public String getId() {
        return id;
    }
    public String getUserId() {
        return userId;
    }
    public String getName() {
        return userName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public AccountInformation toAccountInformation() {
        return new AccountInformation(userId, userName, email);
    }
    public static String hashPassword(String password) {
        return HashUtils.toHash(password + PasswordSalt, "sha512", "hex", 17).toUpperCase();
    }
}
