package com.NetworkClash.demo.config;

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
                // Mematikan pelindung CSRF (wajib dimatikan agar LibGDX bisa nge-POST data nanti)
                .csrf(csrf -> csrf.disable())
                // Mengizinkan semua orang (termasuk browser & LibGDX) mengakses URL apa pun tanpa login
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}