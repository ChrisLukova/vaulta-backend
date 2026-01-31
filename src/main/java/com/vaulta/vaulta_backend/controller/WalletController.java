package com.vaulta.vaulta_backend.controller;

import com.vaulta.vaulta_backend.dto.WalletResponse;
import com.vaulta.vaulta_backend.model.User;
import com.vaulta.vaulta_backend.model.Wallet;
import com.vaulta.vaulta_backend.service.UserService;
import com.vaulta.vaulta_backend.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    // GET wallet info of logged-in user
    @GetMapping
    public ResponseEntity<WalletResponse> getWallet(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletService.getWalletByUser(user);

        return ResponseEntity.ok(
                new WalletResponse(wallet.getBalance(), wallet.getCurrency())
        );
    }

    // Deposit
    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> deposit(
            Authentication authentication,
            @RequestParam BigDecimal amount
            ) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletService.deposit(user, amount);

        return ResponseEntity.ok(
                new WalletResponse(wallet.getBalance(), wallet.getCurrency())
        );
    }

    // Withdraw
    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            Authentication authentication,
            @RequestParam BigDecimal amount
    ) {
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Wallet wallet = walletService.withdraw(user, amount);

        return ResponseEntity.ok(
                new WalletResponse(wallet.getBalance(), wallet.getCurrency())
        );
    }
}
