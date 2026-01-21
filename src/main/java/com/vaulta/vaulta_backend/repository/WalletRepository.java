package com.vaulta.vaulta_backend.repository;

import com.vaulta.vaulta_backend.model.User;
import com.vaulta.vaulta_backend.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);
}
