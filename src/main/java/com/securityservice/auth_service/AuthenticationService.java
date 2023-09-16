package com.securityservice.auth_service;

import com.securityservice.auth_request_response.AuthenticationRequest;
import com.securityservice.auth_request_response.AuthenticationResponse;
import com.securityservice.auth_request_response.RegisterRequest;
import com.securityservice.security_configuration.JWTService;
import com.securityservice.model.PasswordReset;
import com.securityservice.model.User;
import com.securityservice.password_reset_repository.PasswordResetRepository;
import com.securityservice.user_repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private final JWTService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private PasswordResetRepository passwordResetRepository;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .name(registerRequest.getName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .jwt(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        var user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .jwt(jwtToken)
                .build();
    }

    public String generateCode(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow();
        String passwordResetCode = null;
        if (user != null) {
            Random random = new Random();
            int randomNumber = random.nextInt(1000000000);
            passwordResetCode = Integer.toString(randomNumber);
        }
        return passwordResetCode;
    }

    public void sendCode(String username) {
        String code = generateCode(username);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(username);
        message.setSubject("Your password reset code is provided below");
        message.setText(code);

        javaMailSender.send(message);

        Optional<PasswordReset> optionalPasswordReset = passwordResetRepository.findByUsername(username);
        if (optionalPasswordReset.isPresent()){
            PasswordReset passwordReset = optionalPasswordReset.get();
            if (username.equals(passwordReset.getUsername())){
                passwordReset.setCode(code);
                passwordResetRepository.save(passwordReset);
            }
        }
        else {
            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setUsername(username);
            passwordReset.setCode(code);
            passwordResetRepository.save(passwordReset);
        }
    }

    public String changePassword(String resetCode, String username, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<PasswordReset> passwordResetOptional = passwordResetRepository.findByUsername(username);
        if (userOptional.isPresent() && passwordResetOptional.isPresent()) {
            User user = userOptional.get();
            PasswordReset passwordReset = passwordResetOptional.get();
            if (resetCode.equals(passwordReset.getCode())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return "Password changed successfully";
            } else {
                return "Invalid reset code";
            }
        } else {
            return "User or reset code not found";
        }
    }
}
