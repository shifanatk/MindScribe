package com.mindscribe.controller;

import com.mindscribe.model.User;
import com.mindscribe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password) {

        if (userRepository.findByUsername(username).isPresent()) {
            return "{\"error\":\"Username already taken\"}";
        }

        String encoded = passwordEncoder.encode(password);
        User user = new User(username, encoded, "ROLE_USER");
        userRepository.save(user);

        return "{\"status\":\"registered\"}";
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
}

