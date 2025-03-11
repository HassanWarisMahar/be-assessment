package com.assessment.task.security;

import com.assessment.task.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        log.info("Configuring SecurityFilterChain...");

        try {
            return http
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                    .headers(headersConfigurer -> {
                        headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
                        log.info("H2 Console frame options disabled.");
                    })
                    .authorizeHttpRequests(auth -> {
                        auth.requestMatchers("/api/auth/**").permitAll();
                        auth.requestMatchers("/h2-console/**").permitAll();
                        auth.anyRequest().authenticated();
                        log.info("Defined authorization rules: Public access for /api/auth/** and /h2-console/**, all other requests require authentication.");
                    })
                    .sessionManagement(session -> {
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                        log.info("Session management set to STATELESS.");
                    })
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        } catch (Exception e) {
            log.error("Error configuring security filter chain", e);
            throw e;
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        log.info("Initializing AuthenticationManager...");
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Initializing BCryptPasswordEncoder...");
        return new BCryptPasswordEncoder();
    }
}
