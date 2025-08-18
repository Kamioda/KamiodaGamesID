package com.kamioda.id.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.kamioda.id.model.SequenceNumber;

public interface SequenceNumberRepository extends JpaRepository<SequenceNumber, Long> {
    @Transactional(readOnly = true)
    @Query("SELECT s FROM SequenceNumber s WHERE s.frontIndex = :frontIndex AND s.serialText = :serialText")
    SequenceNumber find(String frontIndex, String serialText);
    @Modifying
    @Transactional
    @Query("INSERT INTO SequenceNumber (frontIndex, serialText, sequenceNumber) VALUES (:frontIndex, :serialText, 0)")
    int add(String frontIndex, String serialText);
    @Modifying
    @Transactional
    @Query("UPDATE SequenceNumber s SET s.sequenceNumber = s.sequenceNumber + 1 WHERE s.frontIndex = :frontIndex AND s.serialText = :serialText")
    int increment(String frontIndex, String serialText);
}
