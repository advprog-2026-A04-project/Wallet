package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.dto.WalletTransactionDTO;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import id.ac.ui.cs.a04.json.wallet.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {
    private WalletTransactionRepository transactionRepository;

    @Autowired
    public WalletTransactionServiceImpl(WalletTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public WalletTransaction createTransaction(WalletTransactionDTO transactionDto) {
        WalletTransaction transaction = WalletTransaction.builder()
                .userId(transactionDto.getUserId())
                .type(transactionDto.getType())
                .direction(transactionDto.getDirection())
                .amount(transactionDto.getAmount())
                .status(transactionDto.getStatus())
                .refType(transactionDto.getRefType())
                .refId(transactionDto.getRefId())
                .build();
        transactionRepository.save(transaction);
        return transaction;
    }

    @Override
    public List<WalletTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
