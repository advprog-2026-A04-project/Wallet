package id.ac.ui.cs.a04.json.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import id.ac.ui.cs.a04.json.wallet.model.Wallet;
import id.ac.ui.cs.a04.json.wallet.repository.TopUpRequestRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WalletRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WalletTransactionRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WithdrawalRequestRepository;
import id.ac.ui.cs.a04.json.wallet.service.WalletService;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
class WalletDeductConcurrencyTest {

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
    void concurrentDeductShouldNeverDriveBalanceNegative() throws InterruptedException {
        walletRepository.save(new Wallet(321L, new BigDecimal("1000000")));

        int threads = 25;
        BigDecimal amount = new BigDecimal("100000");
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failedCount = new AtomicInteger();

        try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                final long orderId = 9000L + i;
                executorService.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        try {
                            walletService.deduct(321L, orderId, amount);
                            successCount.incrementAndGet();
                        } catch (ResponseStatusException exception) {
                            failedCount.incrementAndGet();
                        }
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }

            ready.await();
            start.countDown();
            done.await();
        }

        Wallet wallet = walletRepository.findById(321L).orElseThrow();
        int recordedPayments = walletTransactionRepository.findAllByUserIdOrderByCreatedAtDesc(321L).stream()
                .filter(tx -> tx.getType() == TransactionType.PAYMENT)
                .filter(tx -> tx.getRefType() == TransactionReferenceType.ORDER)
                .toList()
                .size();

        assertEquals(threads, successCount.get() + failedCount.get());
        assertTrue(wallet.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        assertEquals(successCount.get(), recordedPayments);
        assertTrue(wallet.getBalance().compareTo(
                new BigDecimal("1000000").subtract(amount.multiply(BigDecimal.valueOf(successCount.get())))
        ) == 0);
    }
}
