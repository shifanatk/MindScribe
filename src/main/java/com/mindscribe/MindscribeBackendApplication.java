package com.mindscribe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class MindscribeBackendApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;   // <-- add this field

    public static void main(String[] args) {
        SpringApplication.run(MindscribeBackendApplication.class, args);
    }

    @PostConstruct
    public void printHash() {
        System.out.println(passwordEncoder.encode("mind123"));
    }
}
