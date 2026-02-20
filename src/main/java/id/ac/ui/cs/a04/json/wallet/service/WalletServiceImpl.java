package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.dto.WalletTransactionDTO;
import id.ac.ui.cs.a04.json.wallet.model.TransactionDirection;
import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionStatus;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import id.ac.ui.cs.a04.json.wallet.model.Wallet;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
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

    private WalletTransactionService transactionService;
    private WalletRepository walletRepository;

    @Autowired
    public WalletServiceImpl(WalletTransactionService transactionService, WalletRepository walletRepository) {
        this.transactionService = transactionService;
        this.walletRepository = walletRepository;
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
}
