package com.mindscribe.controller;

import com.mindscribe.dto.SignupRequest;
import com.mindscribe.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager,
                          AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        if (auth.isAuthenticated()) {
            return "{\"status\":\"logged_in\"}";
        } else {
            return "{\"error\":\"invalid_credentials\"}";
        }
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        try {
            authService.registerUser(request);
            return "{\"status\":\"registered\"}";
        } catch (IllegalArgumentException ex) {
            return "{\"error\":\"" + ex.getMessage() + "\"}";
        }
    }
}

