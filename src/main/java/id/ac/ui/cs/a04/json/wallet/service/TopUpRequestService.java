package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.model.TopUpRequest;
import id.ac.ui.cs.a04.json.wallet.model.TransactionStatus;

import java.math.BigDecimal;

public interface TopUpRequestService {
    public TopUpRequest topUp(Long userId, BigDecimal amount);
    public TransactionStatus updateStatus(Long id, TransactionStatus status);
}
