package com.kamioda.id.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kamioda.id.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    @Transactional(readOnly = true)
    @Query("SELECT t FROM Token t WHERE t.accessToken = :accessToken")
    Token findByAccessToken(@Param("accessToken") String accessToken);
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(t) FROM Token t WHERE t.accessToken = :accessToken")
    int countByAccessToken(@Param("accessToken") String accessToken);
    @Transactional(readOnly = true)
    @Query("SELECT t FROM Token t WHERE t.refreshToken = :refreshToken")
    Token findByRefreshToken(@Param("refreshToken") String refreshToken);
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(t) FROM Token t WHERE t.refreshToken = :refreshToken")
    int countByRefreshToken(@Param("refreshToken") String refreshToken);
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.accessToken = :accessToken")
    int deleteByAccessToken(@Param("accessToken") String accessToken);
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.refreshToken = :refreshToken")
    int deleteByRefreshToken(@Param("refreshToken") String refreshToken);
}
