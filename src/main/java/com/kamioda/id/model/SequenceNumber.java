package com.kamioda.id.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "sequencenumber",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_sequence_id", columnNames = "ID")
    }
)
public class SequenceNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;
    @Column(name = "FrontIndex", nullable = false)
    @Size(max = 2, min = 2)
    @Pattern(regexp = "^[0-9]{2}$", message = "FrontIndex must be exactly 2 digits")
    private String frontIndex;
    @Column(name = "SerialText", nullable = false)
    @Size(max = 3, min = 3)
    @Pattern(regexp = "^[0-9]{3}$", message = "SerialText must be exactly 3 digits")
    private String serialText;
    @Column(name = "SequenceNumber", nullable = false)
    @Max(value = 999999, message = "SequenceNumber must be less than or equal to 999999")
    @Min(value = 0, message = "SequenceNumber must be greater than or equal to 0")
    private Long sequenceNumber;
    public SequenceNumber() {}
    public Long getSequenceNumber() {
        return sequenceNumber;
    }
}

