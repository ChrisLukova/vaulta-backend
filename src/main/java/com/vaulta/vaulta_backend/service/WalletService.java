package com.vaulta.vaulta_backend.service;

import com.vaulta.vaulta_backend.model.User;
import com.vaulta.vaulta_backend.model.Wallet;
import com.vaulta.vaulta_backend.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
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
    public Wallet getWalletByUser(User user) {
        Optional<Wallet> wallet = walletRepository.findByUser(user);
        return wallet.orElseThrow(() -> new RuntimeException("Wallet not found"));
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
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletRepository.save(wallet);
    }
}
