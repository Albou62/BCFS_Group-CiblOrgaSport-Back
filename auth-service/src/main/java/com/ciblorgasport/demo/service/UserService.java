package com.ciblorgasport.demo.service;

import com.ciblorgasport.demo.entity.User;
import com.ciblorgasport.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

 private final UserRepository userRepository;
 private final PasswordEncoder passwordEncoder;

 public UserService(UserRepository userRepository,
                    PasswordEncoder passwordEncoder) {
     this.userRepository = userRepository;
     this.passwordEncoder = passwordEncoder;
 }

 // /register : tout le monde arrive en SPECTATEUR
 public User register(String username, String rawPassword) {
     User user = new User();
     user.setUsername(username);
     user.setPassword(passwordEncoder.encode(rawPassword));
     user.setRole(User.Role.SPECTATEUR);
     return userRepository.save(user);
 }

 // utilisé par l'admin pour changer le rôle
 public User updateRole(Long userId, User.Role newRole) {
     User user = userRepository.findById(userId)
             .orElseThrow(() -> new RuntimeException("User not found"));
     user.setRole(newRole);
     return userRepository.save(user);
 }

 // utilisé par Spring Security
 @Override
 public UserDetails loadUserByUsername(String username)
         throws UsernameNotFoundException {

     User user = userRepository.findByUsername(username)
             .orElseThrow(() -> new UsernameNotFoundException("User not found"));

     return org.springframework.security.core.userdetails.User
             .withUsername(user.getUsername())
             .password(user.getPassword())
             .roles(user.getRole().name())
             .build();
 }
 
 public User findByUsername(String username) {
	    return userRepository.findByUsername(username)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

}
