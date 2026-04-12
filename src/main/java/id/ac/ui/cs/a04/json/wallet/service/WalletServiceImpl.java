package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.dto.WalletTransactionDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private WalletTransactionService transactionService;

    private WalletRepository walletRepository;
    private TopUpRequestRepository topUpRequestRepository;
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Autowired
    public WalletServiceImpl(WalletTransactionService transactionService, WalletRepository walletRepository, TopUpRequestRepository topUpRequestRepository, WithdrawalRequestRepository withdrawalRequestRepository) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
        this.topUpRequestRepository = topUpRequestRepository;
        this.withdrawalRequestRepository = withdrawalRequestRepository;
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
        transactionService.createTransaction(WalletTransactionDTO.builder()
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
        WithdrawalRequest result = withdrawalRequestRepository.save(WithdrawalRequest.builder()
                .userId(userId)
                .amount(amount)
                .destination(destination)
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build());
        return result.getId();
    }

    @Override
    public boolean markWithdrawSuccess(Long withdrawalId) {
        // TODO: Atomicity
        WithdrawalRequest request = withdrawalRequestRepository.getReferenceById(withdrawalId);
        if (request.getStatus() != TransactionStatus.PENDING) {
            return false;
        }
        request.setStatus(TransactionStatus.SUCCESS);
        withdrawalRequestRepository.save(request);
        transactionService.createTransaction(WalletTransactionDTO.builder()
                .userId(request.getUserId())
                .type(TransactionType.WITHDRAWAL)
                .direction(TransactionDirection.CREDIT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .refType(TransactionReferenceType.WITHDRAWAL_REQUEST)
                .refId(request.getId())
                .build());
        return true;
    }

    @Override
    public boolean markWithdrawFailed(Long withdrawalId) {
        WithdrawalRequest request = withdrawalRequestRepository.getReferenceById(withdrawalId);
        if (request.getStatus() != TransactionStatus.PENDING) {
            return false;
        }
        request.setStatus(TransactionStatus.FAILED);
        withdrawalRequestRepository.save(request);
        return true;
    }
}
