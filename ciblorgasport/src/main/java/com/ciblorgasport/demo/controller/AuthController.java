package com.ciblorgasport.demo.controller;

import com.ciblorgasport.demo.dto.LoginRequest;
import com.ciblorgasport.demo.dto.TokenResponse;
import com.ciblorgasport.demo.security.JwtService;
import com.ciblorgasport.demo.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UserService userService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody LoginRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        return "OK";
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());
        authManager.authenticate(authToken);

        UserDetails userDetails =
                userService.loadUserByUsername(request.getUsername());
        String jwt = jwtService.generateToken(userDetails);
        return new TokenResponse(jwt);
    }
}
