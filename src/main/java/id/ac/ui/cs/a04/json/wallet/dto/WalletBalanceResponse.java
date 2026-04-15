package id.ac.ui.cs.a04.json.wallet.dto;

import java.math.BigDecimal;

public record WalletBalanceResponse(
        Long userId,
        BigDecimal balance,
        String currency
) {
}
