package com.securityservice.auth_controller;

import com.securityservice.auth_service.AuthenticationService;
import com.securityservice.auth_request_response.AuthenticationRequest;
import com.securityservice.auth_request_response.AuthenticationResponse;
import com.securityservice.auth_request_response.RegisterRequest;
import com.securityservice.password_reset_service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PasswordResetService passwordResetService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("/password-reset/{username}")
    public ResponseEntity<Void> sendCode(@PathVariable String username){
        authenticationService.sendCode(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-change/{resetCode}/{username}/{newPassword}")
    public ResponseEntity<String> changePassword(@PathVariable String resetCode, @PathVariable String username, @PathVariable String newPassword){
        return ResponseEntity.ok(authenticationService.changePassword(resetCode, username, newPassword));
    }
}
