package com.vaulta.vaulta_backend.service;

import com.vaulta.vaulta_backend.model.User;
import com.vaulta.vaulta_backend.model.Wallet;
import com.vaulta.vaulta_backend.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    // Auto-create wallet for a new user
    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("USD"); // default currency
        wallet.setStatus("ACTIVE");

        return walletRepository.save(wallet);
    }

    // Helper: get wallet by user
    @Transactional(readOnly = true)
    public Wallet getWalletByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    // Deposit funds
    public Wallet deposit(User user, BigDecimal amount) {
        Wallet wallet = getWalletByUser(user);
        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    // Withdraw funds
    public Wallet withdraw(User user, BigDecimal amount) {
        Wallet wallet = getWalletByUser(user);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than zero");
        }
        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new RuntimeException("Wallet is frozen!");
        }
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletRepository.save(wallet);
    }
}
