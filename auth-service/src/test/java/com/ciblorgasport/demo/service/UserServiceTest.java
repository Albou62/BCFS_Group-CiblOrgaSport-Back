package com.ciblorgasport.demo.service;

import com.ciblorgasport.demo.entity.User;
import com.ciblorgasport.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final UserService userService = new UserService(userRepository, passwordEncoder);

    @Test
    void register_creeUnSpectateurAvecMotDePasseEncode() {
        when(passwordEncoder.encode("pwd")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User u = userService.register("alice", "pwd");

        assertThat(u.getUsername()).isEqualTo("alice");
        assertThat(u.getPassword()).isEqualTo("ENCODED");
        assertThat(u.getRole()).isEqualTo(User.Role.SPECTATEUR);
    }

    @Test
    void loadUserByUsername_retourneUserDetailsAvecRole() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword("secret");
        user.setRole(User.Role.RESPONSABLE);

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("bob");

        assertThat(details.getUsername()).isEqualTo("bob");
        assertThat(details.getPassword()).isEqualTo("secret");
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_RESPONSABLE");
    }

    @Test
    void loadUserByUsername_lanceExceptionSiInconnu() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("ghost"));
    }
}
