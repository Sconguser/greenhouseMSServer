package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.configuration.JwtConfig;
import com.greenhouse.greenhouse.models.Role;
import com.greenhouse.greenhouse.models.UserEntity;
import com.greenhouse.greenhouse.repositories.UserRepository;
import com.greenhouse.greenhouse.responses.LoginResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService (AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                        UserRepository userRepository, JwtService jwtService)
    {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse authenticateAndGenerateToken (String username, String password) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Fetch user details from the database
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate the JWT token
        String token = jwtService.generateToken(username, user.getRole()
                .name());

        // Return the token and user details
        return new LoginResponse(token, user.getUsername(), user.getRole()
                .name());
    }

    public boolean register (String username, String password) {
        if (userRepository.findByUsername(username)
                .isPresent())
        {
            return false;
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        userRepository.save(user);
        return true;
    }
}