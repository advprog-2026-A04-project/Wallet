package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.dto.WalletTransactionDTO;
import id.ac.ui.cs.a04.json.wallet.model.TopUpRequest;
import id.ac.ui.cs.a04.json.wallet.model.TransactionDirection;
import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionStatus;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import id.ac.ui.cs.a04.json.wallet.model.Wallet;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import id.ac.ui.cs.a04.json.wallet.repository.TopUpRequestRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WalletRepository;
import id.ac.ui.cs.a04.json.wallet.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletTransactionService walletTransactionService;
    private WalletTransactionService transactionService;

    private WalletRepository walletRepository;
    private TopUpRequestRepository topUpRequestRepository;

    @Autowired
    public WalletServiceImpl(WalletTransactionService transactionService, WalletRepository walletRepository, TopUpRequestRepository topUpRequestRepository, WalletTransactionService walletTransactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
        this.topUpRequestRepository = topUpRequestRepository;
        this.walletTransactionService = walletTransactionService;
    }

    @Override
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.getReferenceById(userId);
    }

    @Override
    public List<WalletTransaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @Override
    public BigDecimal deduct(Long userId, Long orderId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        // TODO: Safely revert mutation if createTransaction fails
        BigDecimal newBalance = wallet.decreaseBalance(amount);
        transactionService.createTransaction(WalletTransactionDTO.builder()
                .userId(userId)
                .type(TransactionType.PAYMENT)
                .direction(TransactionDirection.CREDIT)
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .refType(TransactionReferenceType.ORDER)
                .refId(orderId)
                .build());
        return newBalance;
    }

    @Override
    public BigDecimal refund(Long userId, Long orderId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        // TODO: Safely revert mutation if createTransaction fails
        BigDecimal newBalance = wallet.increaseBalance(amount);
        transactionService.createTransaction(WalletTransactionDTO.builder()
                .userId(userId)
                .type(TransactionType.REFUND)
                .direction(TransactionDirection.DEBIT)
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .refType(TransactionReferenceType.ORDER)
                .refId(orderId)
                .build());
        return newBalance;
    }

    @Override
    public Long createTopUpRequest(Long userId, BigDecimal amount) {
        TopUpRequest result = topUpRequestRepository.save(TopUpRequest.builder()
                .userId(userId)
                .amount(amount)
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build());
        return result.getId();
    }

    @Override
    public boolean markTopUpSuccess(Long topUpId) {
        // TODO: Atomicity
        TopUpRequest request = topUpRequestRepository.getReferenceById(topUpId);
        if (request.getStatus() != TransactionStatus.PENDING) {
            return false;
        }
        request.setStatus(TransactionStatus.SUCCESS);
        topUpRequestRepository.save(request);
        walletTransactionService.createTransaction(WalletTransactionDTO.builder()
                .userId(request.getUserId())
                .type(TransactionType.TOPUP)
                .direction(TransactionDirection.DEBIT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .refType(TransactionReferenceType.TOPUP_REQUEST)
                .refId(request.getId())
                .build());
        return true;
    }

    @Override
    public boolean markTopUpFailed(Long topUpId) {
        TopUpRequest request = topUpRequestRepository.getReferenceById(topUpId);
        if (request.getStatus() != TransactionStatus.PENDING) {
            return false;
        }
        request.setStatus(TransactionStatus.FAILED);
        topUpRequestRepository.save(request);
        return true;
    }

    @Override
    public Long createWithdrawRequest(Long userId, BigDecimal amount, String destination) {
        // TODO: Functionality
        return 0L;
    }

    @Override
    public boolean markWithdrawSuccess(Long topUpId) {
        // TODO: Functionality
        return true;
    }

    @Override
    public boolean markWithdrawFailed(Long topUpId) {
        // TODO: Functionality
        return true;
    }
}
