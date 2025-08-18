package com.kamioda.id.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kamioda.id.model.PreEntryRecord;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PreEntryRepository extends JpaRepository<PreEntryRecord, String> {
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(p) FROM PreEntryRecord p WHERE p.email = :email")
    int countByEmail(@Param("email") String email);
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO PreEntryRecord (PreEntryID, Email) VALUES (:preEntryId, :email)", nativeQuery = true)
    int insertPreEntryRecord(@Param("preEntryId") String preEntryId, @Param("email") String email);
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO PreEntryRecord (PreEntryID, MasterID, Email) VALUES (:preEntryId, :masterId, :email)", nativeQuery = true)
    int insertEmailUpdateRecord(@Param("preEntryId") String updateId, @Param("masterId") String masterId, @Param("email") String email);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM PreEntryRecord p WHERE p.preEntryId = :preEntryId", nativeQuery = true)
    int deletePreEntryRecord(@Param("preEntryId") String preEntryId);
}
