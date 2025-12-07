package com.ciblorgasport.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.demo.entity.User;
import com.ciblorgasport.demo.service.UserService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final UserService userService;

    public RoleController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/user")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/user/{id}")
    public User changeUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        return userService.updateUserRole(id, role);
    }
}
