package com.securityservice.controller;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/demo")
public class DemoController {
    @GetMapping("/hi")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<String> sayHi(){
        return ResponseEntity.ok("Hi");
    }

    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello");
    }
}
