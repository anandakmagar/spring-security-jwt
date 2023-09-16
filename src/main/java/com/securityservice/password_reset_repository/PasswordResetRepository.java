package com.securityservice.password_reset_repository;

import com.securityservice.model.PasswordReset;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByUsername(String username);
    Optional<PasswordReset> findByCode(String code);
}
