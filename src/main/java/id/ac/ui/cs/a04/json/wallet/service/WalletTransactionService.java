package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.dto.WalletTransactionDTO;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;

import java.util.List;

public interface WalletTransactionService {
    WalletTransaction createTransaction(WalletTransactionDTO transactionDto);
    List<WalletTransaction> getAllTransactions();
}
