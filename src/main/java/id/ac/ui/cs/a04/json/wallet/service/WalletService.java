package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.dto.TopUpRequestDto;
import id.ac.ui.cs.a04.json.wallet.dto.WalletBalanceResponse;
import id.ac.ui.cs.a04.json.wallet.dto.WithdrawRequestDto;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    WalletBalanceResponse getBalance(Long userId);
    List<WalletTransaction> getTransactions(Long userId);
    Long createTopUpRequest(TopUpRequestDto request);
    boolean markTopUpSuccess(Long topUpId);
    boolean markTopUpFailed(Long topUpId);
    Long createWithdrawRequest(WithdrawRequestDto request);
    boolean markWithdrawSuccess(Long withdrawalId);
    boolean markWithdrawFailed(Long withdrawalId);
    WalletBalanceResponse deduct(Long userId, Long orderId, BigDecimal amount);
    WalletBalanceResponse refund(Long userId, Long orderId, BigDecimal amount);
}
