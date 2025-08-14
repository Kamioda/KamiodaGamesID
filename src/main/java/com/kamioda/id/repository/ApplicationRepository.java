package com.kamioda.id.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kamioda.id.model.Application;
import com.kamioda.id.model.dto.ApplicationInformation;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {
    @Transactional(readOnly = true)
    @Query("""
        select new com.kamioda.id.model.dto.ApplicationInformation(
            a.appId,
            a.appName,
            a.appDescription,
            a.redirectUri,
            d.userName
        )
        from Application a
        join a.developer d
        where a.appId = :appId
        """)
    ApplicationInformation findApplicationInformationById(@Param("appId") String appId);
    @Transactional(readOnly = true)
    @Query("""
        select new com.kamioda.id.model.dto.ApplicationInformation(
            a.appId,
            a.appName,
            a.appDescription,
            a.redirectUri,
            d.userName
        )
        from Application a
        join a.developer d
        where d.id = :developerId
        """)
    List<ApplicationInformation> findApplicationInformationByDeveloperId(@Param("developerId") String developerId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update Application a
           set a.appSecretHashString = COALESCE(:appSecretHashString, a.appSecretHashString),
               a.appName            = COALESCE(:appName, a.appName),
               a.appDescription     = COALESCE(:appDescription, a.appDescription),
               a.redirectUri        = COALESCE(:redirectUri, a.redirectUri)
         where a.appId = :appId
        """)
    int updateApplication(
        @Param("appId") String appId,
        @Param("appSecretHashString") String appSecretHashString,
        @Param("appName") String appName,
        @Param("appDescription") String appDescription,
        @Param("redirectUri") String redirectUri
    );
}
