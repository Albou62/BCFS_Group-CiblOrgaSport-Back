package com.ciblorgasport.demo.dto;

import com.ciblorgasport.demo.entity.User;

public class RoleUpdateRequest {
 private User.Role role;

 public User.Role getRole() { return role; }
 public void setRole(User.Role role) { this.role = role; }
}

