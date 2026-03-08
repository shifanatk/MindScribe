package com.mindscribe.controller;

import com.mindscribe.model.mysql.User;
import com.mindscribe.repository.mysql.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public record RegisterRequest(String username, String password) {}

    /**
     * Registers a new user in MongoDB Atlas-backed "users" collection.
     * Password is stored as BCrypt hash so that Spring Security can validate it.
     */
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        if (request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return "{\"error\":\"username_and_password_required\"}";
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            return "{\"error\":\"username_taken\"}";
        }

        String hashed = passwordEncoder.encode(request.password());
        User user = new User(request.username(), hashed, "ROLE_USER");
        userRepository.save(user);

        return "{\"status\":\"registered\"}";
    }
}
