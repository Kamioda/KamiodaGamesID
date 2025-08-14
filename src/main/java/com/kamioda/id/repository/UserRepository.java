package com.kamioda.id.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kamioda.id.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Transactional(readOnly = true)
    @Query("SELECT u FROM User u WHERE u.userId = :userId OR u.email = :userId")
    User findByUserId(@Param("userId") String userId);
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(u) FROM User u WHERE u.userId = :userId OR u.email = :userId")
    Long countRecords(@Param("userId") String userId);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userName = COALESCE(:userName, u.userName), u.userId = COALESCE(:userId, u.userId), u.email = COALESCE(:email, u.email), u.password = COALESCE(:password, u.password) WHERE u.id = :id")
    long updateUser(@Param("id") String id, @Param("userId") String userId, @Param("userName") String userName, @Param("email") String email, @Param("password") String password);
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.id = :id")
    long deleteUser(@Param("id") String id);
}
