package com.ciblorgasport.demo.dto;

import com.ciblorgasport.demo.entity.User;

public class UserDto {
 private Long id;
 private String username;
 private User.Role role;

 public UserDto(Long id, String username, User.Role role) {
     this.id = id;
     this.username = username;
     this.role = role;
 }

 public Long getId() { return id; }
 public String getUsername() { return username; }
 public User.Role getRole() { return role; }

 public void setId(Long id) { this.id = id; }
 public void setUsername(String username) { this.username = username; }
 public void setRole(User.Role role) { this.role = role; }
}

