package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.model.Wallet;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    public Wallet getWalletByUserId(Long userId);
    public List<WalletTransaction> getAllTransactions();
    public BigDecimal deduct(Long userId, Long orderId, BigDecimal amount);
    public BigDecimal refund(Long userId, Long orderId, BigDecimal amount);
}
