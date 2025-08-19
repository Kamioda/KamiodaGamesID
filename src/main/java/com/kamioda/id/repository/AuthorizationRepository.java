package com.kamioda.id.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kamioda.id.model.Authorization;

@Repository
public interface AuthorizationRepository extends JpaRepository<Authorization, String> {}
