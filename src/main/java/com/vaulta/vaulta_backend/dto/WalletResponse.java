package com.vaulta.vaulta_backend.dto;

import java.math.BigDecimal;

public record WalletResponse(
        BigDecimal balance,
        String currency
) {
}
