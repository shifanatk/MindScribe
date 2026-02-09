package com.mindscribe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1) Disable CSRF for development / APIs
            .csrf(csrf -> csrf.disable())

            // 2) Open API + H2 console
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/diary/**", "/h2-console/**").permitAll()
                .anyRequest().permitAll()   // or .authenticated() if you later add auth
            )

            // 3) Allow H2 console to render in a frame
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        // No formLogin() needed for this simple API setup

        return http.build();
    }
}



