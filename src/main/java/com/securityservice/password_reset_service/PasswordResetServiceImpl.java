package com.securityservice.password_reset_service;

import com.securityservice.model.PasswordReset;
import com.securityservice.password_reset_repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService{
    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Override
    public void savePasswordResetCode(PasswordReset passwordReset) {
        passwordResetRepository.save(passwordReset);
    }

    @Override
    public Optional<PasswordReset> findByUsername(String username) {
        return Optional.of(passwordResetRepository.findByUsername(username).get());
    }

    @Override
    public Optional<PasswordReset> findByCode(String code) {
        return Optional.of(passwordResetRepository.findByCode(code).get());
    }
}
