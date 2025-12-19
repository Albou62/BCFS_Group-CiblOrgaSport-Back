package com.ciblorgasport.demo.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_et_extractUsername() {
        UserDetails details = User.withUsername("charlie")
                .password("x")
                .roles("SPECTATEUR")
                .build();

        String token = jwtService.generateToken(details);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("charlie");
    }

    @Test
    void isTokenValid_retourneTruePourBonUtilisateur() {
        UserDetails details = User.withUsername("dora")
                .password("x")
                .roles("RESPONSABLE")
                .build();

        String token = jwtService.generateToken(details);

        assertThat(jwtService.isTokenValid(token, details)).isTrue();
    }
}
