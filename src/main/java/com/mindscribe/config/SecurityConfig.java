package com.mindscribe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1) Temporary in‑memory userDetailsService (no DB)
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new org.springframework.security.core.userdetails.User(
                "rasheeda",
                "{noop}secret123",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    // 2) DaoAuthenticationProvider bean (required by authenticationManager)
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // deprecated but fine
        provider.setUserDetailsService(userDetailsService);
        // No password encoder needed because we use {noop}
        return provider;
    }

    // 3) AuthenticationManager bean using that provider
    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
        return new ProviderManager(List.of(provider));
    }

    // 4) Security filter chain: /api/auth/** is open, everything else requires Basic Auth
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println(">>> Building SecurityFilterChain for MindScribe (TEMP in‑memory)");

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .requestCache(requestCache -> requestCache.disable())
            .formLogin(form -> form.disable())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}












