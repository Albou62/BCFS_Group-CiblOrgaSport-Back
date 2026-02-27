package com.ciblorgasport.demo.service;

import com.ciblorgasport.demo.entity.User;
import com.ciblorgasport.demo.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService implements UserDetailsService {
 private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

 private final UserRepository userRepository;
 private final PasswordEncoder passwordEncoder;
 private final NotificationClient notificationClient;
 private final Counter subscriptionOnRegisterSuccess;
 private final Counter subscriptionOnRegisterFailure;

 public UserService(UserRepository userRepository,
                    PasswordEncoder passwordEncoder,
                    NotificationClient notificationClient,
                    MeterRegistry meterRegistry) {
     this.userRepository = userRepository;
     this.passwordEncoder = passwordEncoder;
     this.notificationClient = notificationClient;
     this.subscriptionOnRegisterSuccess = Counter.builder("subscription_on_register_success").register(meterRegistry);
     this.subscriptionOnRegisterFailure = Counter.builder("subscription_on_register_failure").register(meterRegistry);
 }

 // /register : tout le monde arrive en SPECTATEUR
 @Transactional
 public User register(String username, String rawPassword) {
     User user = new User();
     user.setUsername(username);
     user.setPassword(passwordEncoder.encode(rawPassword));
     user.setRole(User.Role.SPECTATEUR);
     User savedUser = userRepository.save(user);

     try {
         Long incidentGroupId = notificationClient.findIncidentGroupId();
         notificationClient.subscribeUserToGroup(savedUser.getId(), incidentGroupId);
         subscriptionOnRegisterSuccess.increment();
         LOGGER.info("register_subscription_success userId={} groupId={}", savedUser.getId(), incidentGroupId);
     } catch (RuntimeException e) {
         subscriptionOnRegisterFailure.increment();
         LOGGER.error("register_subscription_failure userId={} message={}", savedUser.getId(), e.getMessage());
         throw new IllegalStateException("User registration failed because incident group subscription failed", e);
     }

     return savedUser;
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
