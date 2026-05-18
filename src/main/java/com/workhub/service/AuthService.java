package com.workhub.service;

import com.workhub.dto.LoginRequest;
import com.workhub.dto.LoginResponse;
import com.workhub.dto.UserResponse;
import com.workhub.model.User;
import com.workhub.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final String jwtSecret;
    private final long jwtExpirationMs = 86400000; // 1 day

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       @Value("${jwt.secret}") String jwtSecret) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtSecret = jwtSecret;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("tenantId", user.getTenantId().toString())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        return new LoginResponse(token);
    }

    public UserResponse getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new UserResponse(user.getId(), user.getEmail(), user.getTenantId(), user.getRoles());
    }
}
