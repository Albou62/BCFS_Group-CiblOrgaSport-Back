package com.ciblorgasport.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ciblorgasport.demo.entity.User;
import com.ciblorgasport.demo.repository.UserRepository;

//com.ciblorgasport.demo.config.InitialAdminConfig

@Configuration
public class InitialAdminConfig {

 @Bean
 CommandLineRunner initAdmin(UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
     return args -> {
         if (userRepository.count() == 0) {
             User admin = new User();
             admin.setUsername("responsable");
             admin.setPassword(passwordEncoder.encode("changeme"));
             admin.setRole(User.Role.RESPONSABLE);
             userRepository.save(admin);
         }
     };
 }
}
