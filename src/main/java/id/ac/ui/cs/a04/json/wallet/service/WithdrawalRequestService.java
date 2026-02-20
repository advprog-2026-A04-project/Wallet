package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.model.TransactionStatus;
import id.ac.ui.cs.a04.json.wallet.model.WithdrawalRequest;

import java.math.BigDecimal;

public interface WithdrawalRequestService {
    public WithdrawalRequest withdraw(Long userId, String destination, BigDecimal amount);
    public TransactionStatus updateStatus(Long id, TransactionStatus status);
}
