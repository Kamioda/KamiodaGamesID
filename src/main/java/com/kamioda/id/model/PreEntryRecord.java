package com.kamioda.id.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "preentryrecord",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_preentry_id", columnNames = "PreEntryID"),
        @UniqueConstraint(name = "UK_preentry_master_id", columnNames = "MasterID"),
        @UniqueConstraint(name = "UK_preentry_email", columnNames = "Email")
    }
)
public class PreEntryRecord {
    private static final long minExpireTime = 30;
    @Id
    @Column(name = "PreEntryID", nullable = false, unique = true)
    @Pattern(regexp = "^pei-\\d{12}")
    @Size(min = 16, max = 16, message = "PreEntryID must be 16 characters long")
    private String preEntryId;
    @Column(name = "MasterID", nullable = true, unique = true)
    private String masterId;
    @Column(name = "Email", nullable = false, unique = true)
    @Email(message = "Email must be a valid email address")
    private String email;
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    public PreEntryRecord() {}
    public boolean expired() {
        return createdAt.plusMinutes(minExpireTime).isBefore(LocalDateTime.now());
    }
    public String getEmail() {
        return email;
    }
    public String getMasterId() {
        return masterId;
    }
}
