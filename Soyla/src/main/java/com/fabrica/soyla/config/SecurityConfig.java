package com.fabrica.soyla.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/health", "/error", "/favicon.ico").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/usuarios/registrar").permitAll()
                .requestMatchers("/api/invitaciones/*/validar").permitAll()
                .requestMatchers("/api/invitaciones/*/aceptar").authenticated()
                .requestMatchers("/api/auth/logout").authenticated()
                .requestMatchers("/api/grupos/**").authenticated()
                .requestMatchers("/api/tareas/**").authenticated()
                .requestMatchers("/api/invitaciones/**").authenticated()
                .requestMatchers("/api/usuarios/perfil/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

