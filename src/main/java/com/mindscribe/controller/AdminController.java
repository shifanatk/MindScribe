package com.mindscribe.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final JdbcTemplate jdbcTemplate;

    public AdminController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/fix-password")
    public String fixPassword() {
        String hash = "$2a$10$lMSrz9.5ICYpXLNGm5FKqOQgBorrC7Wcmt9oRBTPPU/vsjLCir8Ky"; // mind123
        int updated = jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE username = ?",
                hash, "rasheeda"
        );
        return "Rows updated: " + updated;
    }
}

