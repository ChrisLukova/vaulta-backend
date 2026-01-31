package com.vaulta.vaulta_backend.controller;


import com.vaulta.vaulta_backend.dto.AuthResponse;
import com.vaulta.vaulta_backend.dto.LoginRequest;
import com.vaulta.vaulta_backend.dto.RegisterRequest;
import com.vaulta.vaulta_backend.model.User;
import com.vaulta.vaulta_backend.security.JwtUtil;
import com.vaulta.vaulta_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserService userService,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(
                request.username(),
                request.email(),
                request.password()
        );

        // Generate token immediately after registration
        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Check password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
