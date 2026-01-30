package com.vaulta.vaulta_backend.service;

import com.vaulta.vaulta_backend.model.Role;
import com.vaulta.vaulta_backend.model.User;
import com.vaulta.vaulta_backend.model.Wallet;
import com.vaulta.vaulta_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService; // to auto-create wallet

    public UserService (
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            WalletService walletService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
    }

    // Register user
    public User registerUser(String username, String email, String rawPassword) {
        //check if username or email exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword)); // hash password
        user.setRole(Role.USER); // default role
        user.setStatus("ACTIVE");

        // Save user
        User savedUser = userRepository.save(user);

        // Create wallet automatically
        Wallet wallet = walletService.createWallet(savedUser);

        return savedUser;
    }

    // Find by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Find by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
