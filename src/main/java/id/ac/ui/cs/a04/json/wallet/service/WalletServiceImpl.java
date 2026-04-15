package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.dto.TopUpRequestDto;
import id.ac.ui.cs.a04.json.wallet.dto.WalletBalanceResponse;
import id.ac.ui.cs.a04.json.wallet.dto.WithdrawRequestDto;
import id.ac.ui.cs.a04.json.wallet.model.TopUpRequest;
import id.ac.ui.cs.a04.json.wallet.model.TransactionDirection;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WalletServiceImpl implements WalletService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final TopUpRequestRepository topUpRequestRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;

    public WalletServiceImpl(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository,
            TopUpRequestRepository topUpRequestRepository,
            WithdrawalRequestRepository withdrawalRequestRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.topUpRequestRepository = topUpRequestRepository;
        this.withdrawalRequestRepository = withdrawalRequestRepository;
    }

    @Override
    @Transactional
    public WalletBalanceResponse getBalance(Long userId) {
        return toBalanceResponse(getOrCreateWalletForUpdate(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransaction> getTransactions(Long userId) {
        getOrCreateWallet(userId);
        return transactionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public Long createTopUpRequest(TopUpRequestDto request) {
        getOrCreateWallet(request.userId());

        TopUpRequest result = topUpRequestRepository.save(TopUpRequest.builder()
                .userId(request.userId())
                .amount(request.amount())
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build());
        return result.getId();
    }

    @Override
    @Transactional
    public boolean markTopUpSuccess(Long topUpId) {
        TopUpRequest request = requireTopUpRequest(topUpId);
        if (request.getStatus() != TransactionStatus.PENDING) {
            return false;
        }

        Wallet wallet = getOrCreateWalletForUpdate(request.getUserId());
        wallet.increaseBalance(request.getAmount());
        walletRepository.save(wallet);

        request.setStatus(TransactionStatus.SUCCESS);
        topUpRequestRepository.save(request);

        transactionRepository.save(WalletTransaction.builder()
                .userId(request.getUserId())
                .type(TransactionType.TOPUP)
                .direction(TransactionDirection.CREDIT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .refType(TransactionReferenceType.TOPUP_REQUEST)
                .refId(request.getId())
                .createdAt(LocalDateTime.now())
                .build());
        return true;
    }

    @Override
    @Transactional
    public boolean markTopUpFailed(Long topUpId) {
        TopUpRequest request = requireTopUpRequest(topUpId);
        if (request.getStatus() != TransactionStatus.PENDING) {
            return false;
        }

        request.setStatus(TransactionStatus.FAILED);
        topUpRequestRepository.save(request);
        return true;
    }

    @Override
    @Transactional
    public Long createWithdrawRequest(WithdrawRequestDto request) {
        getOrCreateWallet(request.userId());

        WithdrawalRequest result = withdrawalRequestRepository.save(WithdrawalRequest.builder()
                .userId(request.userId())
                .amount(request.amount())
                .destination(request.destination())
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build());
        return result.getId();
    }

    @Override
    @Transactional
    public boolean markWithdrawSuccess(Long withdrawalId) {
        WithdrawalRequest request = requireWithdrawalRequest(withdrawalId);
        if (request.getStatus() != TransactionStatus.PENDING) {
            return false;
        }

        Wallet wallet = getOrCreateWalletForUpdate(request.getUserId());
        try {
            wallet.decreaseBalance(request.getAmount());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wallet balance is insufficient.");
        }
        walletRepository.save(wallet);

        request.setStatus(TransactionStatus.SUCCESS);
        withdrawalRequestRepository.save(request);

        transactionRepository.save(WalletTransaction.builder()
                .userId(request.getUserId())
                .type(TransactionType.WITHDRAWAL)
                .direction(TransactionDirection.DEBIT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .refType(TransactionReferenceType.WITHDRAWAL_REQUEST)
                .refId(request.getId())
                .createdAt(LocalDateTime.now())
                .build());
        return true;
    }

    @Override
    @Transactional
    public boolean markWithdrawFailed(Long withdrawalId) {
        WithdrawalRequest request = requireWithdrawalRequest(withdrawalId);
        if (request.getStatus() != TransactionStatus.PENDING) {
            return false;
        }

        request.setStatus(TransactionStatus.FAILED);
        withdrawalRequestRepository.save(request);
        return true;
    }

    @Override
    @Transactional
    public WalletBalanceResponse deduct(Long userId, Long orderId, BigDecimal amount) {
        WalletTransaction existing = transactionRepository.findByUserIdAndTypeAndRefTypeAndRefId(
                userId,
                TransactionType.PAYMENT,
                TransactionReferenceType.ORDER,
                orderId
        ).orElse(null);
        if (existing != null) {
            return toBalanceResponse(getOrCreateWalletForUpdate(userId));
        }

        Wallet wallet = getOrCreateWalletForUpdate(userId);
        try {
            wallet.decreaseBalance(amount);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wallet balance is insufficient.");
        }
        walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .userId(userId)
                .type(TransactionType.PAYMENT)
                .direction(TransactionDirection.DEBIT)
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .refType(TransactionReferenceType.ORDER)
                .refId(orderId)
                .createdAt(LocalDateTime.now())
                .build());
        return toBalanceResponse(wallet);
    }

    @Override
    @Transactional
    public WalletBalanceResponse refund(Long userId, Long orderId, BigDecimal amount) {
        WalletTransaction existing = transactionRepository.findByUserIdAndTypeAndRefTypeAndRefId(
                userId,
                TransactionType.REFUND,
                TransactionReferenceType.ORDER,
                orderId
        ).orElse(null);
        if (existing != null) {
            return toBalanceResponse(getOrCreateWalletForUpdate(userId));
        }

        Wallet wallet = getOrCreateWalletForUpdate(userId);
        wallet.increaseBalance(amount);
        walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .userId(userId)
                .type(TransactionType.REFUND)
                .direction(TransactionDirection.CREDIT)
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .refType(TransactionReferenceType.ORDER)
                .refId(orderId)
                .createdAt(LocalDateTime.now())
                .build());
        return toBalanceResponse(wallet);
    }

    private Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findById(userId)
                .orElseGet(() -> walletRepository.save(new Wallet(userId, ZERO)));
    }

    private Wallet getOrCreateWalletForUpdate(Long userId) {
        return walletRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> walletRepository.saveAndFlush(new Wallet(userId, ZERO)));
    }

    private TopUpRequest requireTopUpRequest(Long topUpId) {
        TopUpRequest request = topUpRequestRepository.findTopUpRequestById(topUpId);
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Top-up request not found.");
        }
        return request;
    }

    private WithdrawalRequest requireWithdrawalRequest(Long withdrawalId) {
        WithdrawalRequest request = withdrawalRequestRepository.findWithdrawalRequestById(withdrawalId);
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Withdrawal request not found.");
        }
        return request;
    }

    private WalletBalanceResponse toBalanceResponse(Wallet wallet) {
        return new WalletBalanceResponse(wallet.getUserId(), wallet.getBalance(), "IDR");
    }
}
