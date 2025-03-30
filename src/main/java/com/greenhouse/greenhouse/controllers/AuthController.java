package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.models.Role;
import com.greenhouse.greenhouse.requests.UserRequest;
import com.greenhouse.greenhouse.responses.LoginResponse;
import com.greenhouse.greenhouse.responses.UserResponse;
import com.greenhouse.greenhouse.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController (AuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody UserRequest userRequest, HttpServletResponse response) {
        boolean registered = authService.register(userRequest.getUsername(), userRequest.getPassword());
        if (!registered) {
            return ResponseEntity.badRequest()
                    .body("User already exists");
        }
        return ResponseEntity.ok("User created");
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login (@RequestBody UserRequest userRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.authenticateAndGenerateToken(userRequest.getUsername(),
                userRequest.getPassword());

        response.setHeader("Authorization", "Bearer " + loginResponse.getToken());
        UserResponse userResponse = new UserResponse(loginResponse.getUsername(), Role.valueOf(loginResponse.getRole()),
                loginResponse.getId());

        return ResponseEntity.ok()
                .body(userResponse);
    }

    @Bean
    public CommandLineRunner authCommandLineRunner (ApplicationContext ctx) {
        return args -> {
            System.out.println("Auth controller initialized");
        };
    }
}
