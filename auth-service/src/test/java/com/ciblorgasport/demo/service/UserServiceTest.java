package com.ciblorgasport.demo.service;

import com.ciblorgasport.demo.entity.User;
import com.ciblorgasport.demo.repository.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final NotificationClient notificationClient = Mockito.mock(NotificationClient.class);
    private final UserService userService = new UserService(
            userRepository,
            passwordEncoder,
            notificationClient,
            new SimpleMeterRegistry()
    );

    @Test
    void register_creeUnSpectateurAvecMotDePasseEncode() {
        when(passwordEncoder.encode("pwd")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });
        when(notificationClient.findIncidentGroupId()).thenReturn(7L);

        User u = userService.register("alice", "pwd");

        verify(notificationClient).subscribeUserToGroup(42L, 7L);
        assertEquals(42L, u.getId());
        assertThat(u.getUsername()).isEqualTo("alice");
        assertThat(u.getPassword()).isEqualTo("ENCODED");
        assertThat(u.getRole()).isEqualTo(User.Role.SPECTATEUR);
    }

    @Test
    void register_lanceExceptionSiSubscriptionEchoue() {
        when(passwordEncoder.encode("pwd")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });
        when(notificationClient.findIncidentGroupId()).thenReturn(3L);
        Mockito.doThrow(new NotificationClient.NotificationClientException("boom"))
                .when(notificationClient).subscribeUserToGroup(99L, 3L);

        assertThrows(IllegalStateException.class, () -> userService.register("alice", "pwd"));
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
