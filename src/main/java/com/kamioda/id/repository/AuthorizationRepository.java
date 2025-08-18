package com.kamioda.id.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kamioda.id.model.Authorization;

@Repository
public interface AuthorizationRepository extends JpaRepository<Authorization, String> {
    @Transactional(readOnly = true)
    @Query("SELECT a FROM Authorization a WHERE a.authID = CONCAT('auth0-', :id)")
    Authorization findByAuthId(@Param("id") String id);
    @Transactional(readOnly = true)
    @Query("SELECT a FROM Authorization a WHERE a.authID = CONCAT('auth1-', :code)")
    Authorization findByAuthCode(@Param("code") String code);
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO authorizations
          (AuthID, AppID, RedirectURI, CodeChallenge, CodeChallengeMethod)
        VALUES (CONCAT('auth0-', :authId), :appId, :redirectURI, :codeChallenge, :codeChallengeMethod)
        """, nativeQuery = true)
    int createAuthorization(@Param("authId") String authId, @Param("appId") String appId, @Param("redirectURI") String redirectURI, @Param("codeChallenge") String codeChallenge, @Param("codeChallengeMethod") String codeChallengeMethod);
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE authorizations
           SET AuthID = CONCAT('auth1-', :authCode),
               AuthorizedID = :masterId
         WHERE AuthID = CONCAT('auth0-', :authId)
        """, nativeQuery = true)
    int updateToAuthCode(@Param("authCode") String authCode, @Param("masterId") String masterId, @Param("authId") String authId);
    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM authorizations
         WHERE AuthID = CONCAT('auth0-', :auth)
            OR AuthID = CONCAT('auth1-', :auth)
        """, nativeQuery = true)
    int deleteByAuthId(@Param("auth") String authId);
}
