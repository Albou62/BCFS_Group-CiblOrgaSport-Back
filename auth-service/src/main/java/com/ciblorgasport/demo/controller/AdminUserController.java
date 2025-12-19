package com.ciblorgasport.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.demo.dto.RoleUpdateRequest;
import com.ciblorgasport.demo.dto.UserDto;
import com.ciblorgasport.demo.entity.User;
import com.ciblorgasport.demo.repository.UserRepository;
import com.ciblorgasport.demo.service.UserService;


@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

 private final UserRepository userRepository;
 private final UserService userService;

 public AdminUserController(UserRepository userRepository,
                            UserService userService) {
     this.userRepository = userRepository;
     this.userService = userService;
 }

 @GetMapping
 @PreAuthorize("hasRole('RESPONSABLE')")
 public List<UserDto> listUsers() {
     return userRepository.findAll().stream()
             .map(u -> new UserDto(u.getId(), u.getUsername(), u.getRole()))
             .toList();
 }

 @PutMapping("/{id}/role")
 @PreAuthorize("hasRole('RESPONSABLE')")
 public UserDto updateRole(@PathVariable Long id,
                           @RequestBody RoleUpdateRequest request) {
     User updated = userService.updateRole(id, request.getRole());
     return new UserDto(updated.getId(), updated.getUsername(), updated.getRole());
 }
}
