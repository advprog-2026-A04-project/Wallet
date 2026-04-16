package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.dto.TopUpRequestDto;
import id.ac.ui.cs.a04.json.wallet.dto.WithdrawRequestDto;
import id.ac.ui.cs.a04.json.wallet.model.TopUpRequest;
import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionStatus;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import id.ac.ui.cs.a04.json.wallet.model.Wallet;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import id.ac.ui.cs.a04.json.wallet.model.WithdrawalRequest;
import id.ac.ui.cs.a04.json.wallet.repository.TopUpRequestRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WalletRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WalletTransactionRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WithdrawalRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletServiceImplTest {

    private WalletRepository walletRepository;
    private WalletTransactionRepository transactionRepository;
    private TopUpRequestRepository topUpRequestRepository;
    private WithdrawalRequestRepository withdrawalRequestRepository;
    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(WalletTransactionRepository.class);
        topUpRequestRepository = mock(TopUpRequestRepository.class);
        withdrawalRequestRepository = mock(WithdrawalRequestRepository.class);
        walletService = new WalletServiceImpl(
                walletRepository,
                transactionRepository,
                topUpRequestRepository,
                withdrawalRequestRepository,
                new WalletTransactionFactory()
        );
    }

    @Test
    void getBalanceShouldCreateWalletWhenMissing() {
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.empty());
        when(walletRepository.saveAndFlush(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = walletService.getBalance(1L);

        assertEquals(new BigDecimal("0"), response.balance());
    }

    @Test
    void getTransactionsShouldEnsureWalletExists() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(new Wallet(1L, BigDecimal.ZERO)));
        when(transactionRepository.findAllByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        walletService.getTransactions(1L);

        verify(transactionRepository).findAllByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void createTopUpRequestShouldPersistPendingRequest() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(new Wallet(1L, BigDecimal.ZERO)));
        when(topUpRequestRepository.save(any(TopUpRequest.class))).thenAnswer(invocation -> {
            TopUpRequest request = invocation.getArgument(0);
            request.setId(11L);
            return request;
        });

        Long id = walletService.createTopUpRequest(new TopUpRequestDto(1L, new BigDecimal("100")));

        assertEquals(11L, id);
    }

    @Test
    void markTopUpSuccessShouldReturnFalseForNonPendingRequest() {
        TopUpRequest request = TopUpRequest.builder().id(3L).userId(1L).amount(new BigDecimal("100")).status(TransactionStatus.SUCCESS).build();
        when(topUpRequestRepository.findTopUpRequestById(3L)).thenReturn(request);

        assertEquals(false, walletService.markTopUpSuccess(3L));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void markTopUpSuccessShouldCreditWalletAndPersistTransaction() {
        TopUpRequest request = TopUpRequest.builder().id(3L).userId(1L).amount(new BigDecimal("100")).status(TransactionStatus.PENDING).build();
        Wallet wallet = new Wallet(1L, new BigDecimal("25"));
        when(topUpRequestRepository.findTopUpRequestById(3L)).thenReturn(request);
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(wallet));

        assertEquals(true, walletService.markTopUpSuccess(3L));
        assertEquals(new BigDecimal("125"), wallet.getBalance());
        verify(transactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void markTopUpFailedShouldHandlePendingAndNonPendingRequests() {
        TopUpRequest pending = TopUpRequest.builder().id(4L).status(TransactionStatus.PENDING).build();
        when(topUpRequestRepository.findTopUpRequestById(4L)).thenReturn(pending);

        assertEquals(true, walletService.markTopUpFailed(4L));
        assertEquals(TransactionStatus.FAILED, pending.getStatus());

        TopUpRequest failed = TopUpRequest.builder().id(5L).status(TransactionStatus.FAILED).build();
        when(topUpRequestRepository.findTopUpRequestById(5L)).thenReturn(failed);
        assertEquals(false, walletService.markTopUpFailed(5L));
    }

    @Test
    void createWithdrawRequestShouldPersistPendingRequest() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(new Wallet(1L, BigDecimal.ZERO)));
        when(withdrawalRequestRepository.save(any(WithdrawalRequest.class))).thenAnswer(invocation -> {
            WithdrawalRequest request = invocation.getArgument(0);
            request.setId(12L);
            return request;
        });

        Long id = walletService.createWithdrawRequest(new WithdrawRequestDto(1L, new BigDecimal("100"), "bank"));

        assertEquals(12L, id);
    }

    @Test
    void markWithdrawSuccessShouldReturnFalseForNonPendingRequest() {
        WithdrawalRequest request = WithdrawalRequest.builder().id(5L).status(TransactionStatus.SUCCESS).build();
        when(withdrawalRequestRepository.findWithdrawalRequestById(5L)).thenReturn(request);

        assertEquals(false, walletService.markWithdrawSuccess(5L));
    }

    @Test
    void markWithdrawSuccessShouldThrowConflictWhenBalanceIsInsufficient() {
        WithdrawalRequest request = WithdrawalRequest.builder()
                .id(5L)
                .userId(1L)
                .amount(new BigDecimal("100"))
                .status(TransactionStatus.PENDING)
                .build();
        when(withdrawalRequestRepository.findWithdrawalRequestById(5L)).thenReturn(request);
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(new Wallet(1L, new BigDecimal("20"))));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> walletService.markWithdrawSuccess(5L));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void markWithdrawSuccessShouldDebitWalletAndPersistTransaction() {
        WithdrawalRequest request = WithdrawalRequest.builder()
                .id(5L)
                .userId(1L)
                .amount(new BigDecimal("50"))
                .status(TransactionStatus.PENDING)
                .build();
        Wallet wallet = new Wallet(1L, new BigDecimal("200"));
        when(withdrawalRequestRepository.findWithdrawalRequestById(5L)).thenReturn(request);
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(wallet));

        assertEquals(true, walletService.markWithdrawSuccess(5L));
        assertEquals(new BigDecimal("150"), wallet.getBalance());
        verify(transactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void markWithdrawFailedShouldHandlePendingAndNonPendingRequests() {
        WithdrawalRequest pending = WithdrawalRequest.builder().id(6L).status(TransactionStatus.PENDING).build();
        when(withdrawalRequestRepository.findWithdrawalRequestById(6L)).thenReturn(pending);

        assertEquals(true, walletService.markWithdrawFailed(6L));
        assertEquals(TransactionStatus.FAILED, pending.getStatus());

        WithdrawalRequest failed = WithdrawalRequest.builder().id(7L).status(TransactionStatus.FAILED).build();
        when(withdrawalRequestRepository.findWithdrawalRequestById(7L)).thenReturn(failed);
        assertEquals(false, walletService.markWithdrawFailed(7L));
    }

    @Test
    void deductShouldBeIdempotentWhenTransactionAlreadyExists() {
        WalletTransaction existing = WalletTransaction.builder().userId(1L).type(TransactionType.PAYMENT).refType(TransactionReferenceType.ORDER).refId(9L).build();
        when(transactionRepository.findByUserIdAndTypeAndRefTypeAndRefId(1L, TransactionType.PAYMENT, TransactionReferenceType.ORDER, 9L))
                .thenReturn(Optional.of(existing));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(new Wallet(1L, new BigDecimal("80"))));

        var response = walletService.deduct(1L, 9L, new BigDecimal("20"));

        assertEquals(new BigDecimal("80"), response.balance());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deductShouldThrowConflictForInsufficientBalance() {
        when(transactionRepository.findByUserIdAndTypeAndRefTypeAndRefId(1L, TransactionType.PAYMENT, TransactionReferenceType.ORDER, 9L))
                .thenReturn(Optional.empty());
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(new Wallet(1L, new BigDecimal("10"))));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> walletService.deduct(1L, 9L, new BigDecimal("20"))
        );

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void deductShouldDebitWalletAndPersistPaymentTransaction() {
        when(transactionRepository.findByUserIdAndTypeAndRefTypeAndRefId(1L, TransactionType.PAYMENT, TransactionReferenceType.ORDER, 9L))
                .thenReturn(Optional.empty());
        Wallet wallet = new Wallet(1L, new BigDecimal("100"));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(wallet));

        var response = walletService.deduct(1L, 9L, new BigDecimal("30"));

        assertEquals(new BigDecimal("70"), response.balance());
        verify(transactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void refundShouldBeIdempotentWhenTransactionAlreadyExists() {
        WalletTransaction existing = WalletTransaction.builder().userId(1L).type(TransactionType.REFUND).refType(TransactionReferenceType.ORDER).refId(9L).build();
        when(transactionRepository.findByUserIdAndTypeAndRefTypeAndRefId(1L, TransactionType.REFUND, TransactionReferenceType.ORDER, 9L))
                .thenReturn(Optional.of(existing));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(new Wallet(1L, new BigDecimal("70"))));

        var response = walletService.refund(1L, 9L, new BigDecimal("30"));

        assertEquals(new BigDecimal("70"), response.balance());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void refundShouldCreditWalletAndPersistTransaction() {
        when(transactionRepository.findByUserIdAndTypeAndRefTypeAndRefId(1L, TransactionType.REFUND, TransactionReferenceType.ORDER, 9L))
                .thenReturn(Optional.empty());
        Wallet wallet = new Wallet(1L, new BigDecimal("70"));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(wallet));

        var response = walletService.refund(1L, 9L, new BigDecimal("30"));

        assertEquals(new BigDecimal("100"), response.balance());
        verify(transactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void missingTopUpRequestShouldReturnNotFound() {
        when(topUpRequestRepository.findTopUpRequestById(99L)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> walletService.markTopUpSuccess(99L));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void missingWithdrawalRequestShouldReturnNotFound() {
        when(withdrawalRequestRepository.findWithdrawalRequestById(99L)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> walletService.markWithdrawSuccess(99L));

        assertEquals(404, exception.getStatusCode().value());
    }
}
