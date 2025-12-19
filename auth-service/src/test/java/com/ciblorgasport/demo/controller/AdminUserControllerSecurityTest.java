package com.ciblorgasport.demo.controller;

import com.ciblorgasport.demo.dto.UserDto;
import com.ciblorgasport.demo.entity.User;
import com.ciblorgasport.demo.repository.UserRepository;
import com.ciblorgasport.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class AdminUserControllerSecurityTest {

    @Autowired
    private AdminUserController adminUserController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "RESPONSABLE")
    void listUsers_accessiblePourResponsable() {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setRole(User.Role.SPECTATEUR);

        when(userRepository.findAll()).thenReturn(List.of(u));

        List<UserDto> result = adminUserController.listUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("alice");
    }

    @Test
    @WithMockUser(roles = "SPECTATEUR")
    void listUsers_interditPourSpectateur() {
        assertThrows(AccessDeniedException.class,
                () -> adminUserController.listUsers());
    }
}
