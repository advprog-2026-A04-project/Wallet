package id.ac.ui.cs.a04.json.wallet;

import static org.junit.jupiter.api.Assertions.assertTrue;

import id.ac.ui.cs.a04.json.wallet.dto.TopUpRequestDto;
import id.ac.ui.cs.a04.json.wallet.dto.WalletBalanceResponse;
import id.ac.ui.cs.a04.json.wallet.repository.TopUpRequestRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WalletRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WalletTransactionRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WithdrawalRequestRepository;
import id.ac.ui.cs.a04.json.wallet.service.WalletService;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WalletFlowIntegrationTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private TopUpRequestRepository topUpRequestRepository;

    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @AfterEach
    void cleanup() {
        walletTransactionRepository.deleteAll();
        topUpRequestRepository.deleteAll();
        withdrawalRequestRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    void topUpAndDeductFlowShouldUpdateBalance() {
        WalletBalanceResponse initialBalance = walletService.getBalance(1000L);
        assertTrue(initialBalance.balance().compareTo(BigDecimal.ZERO) == 0);

        Long topUpId = walletService.createTopUpRequest(new TopUpRequestDto(1000L, new BigDecimal("500000")));
        assertTrue(walletService.markTopUpSuccess(topUpId));

        WalletBalanceResponse afterTopUp = walletService.getBalance(1000L);
        assertTrue(afterTopUp.balance().compareTo(new BigDecimal("500000")) == 0);

        WalletBalanceResponse afterDeduct = walletService.deduct(1000L, 501L, new BigDecimal("125000"));
        assertTrue(afterDeduct.balance().compareTo(new BigDecimal("375000")) == 0);
    }
}
