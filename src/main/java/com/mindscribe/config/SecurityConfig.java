package com.mindscribe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // ✅ Allow H2 Console
                .requestMatchers("/h2-console/**").permitAll()
                // ✅ Allow all diary endpoints
                .requestMatchers("/api/diary/**").permitAll()
                // Everything else needs authentication
                .anyRequest().authenticated()
            )
            // ✅ Disable CSRF for H2
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            // ✅ Disable frame options (H2 uses iframes)
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            )
            .formLogin(Customizer.withDefaults());
        
        return http.build();
    }
}


