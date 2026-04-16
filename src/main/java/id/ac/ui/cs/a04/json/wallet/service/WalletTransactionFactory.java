package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.model.TransactionDirection;
import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionStatus;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class WalletTransactionFactory {

    public WalletTransaction topUpSuccess(Long userId, Long requestId, BigDecimal amount) {
        return build(userId, TransactionType.TOPUP, TransactionDirection.CREDIT, amount,
                TransactionReferenceType.TOPUP_REQUEST, requestId);
    }

    public WalletTransaction withdrawSuccess(Long userId, Long requestId, BigDecimal amount) {
        return build(userId, TransactionType.WITHDRAWAL, TransactionDirection.DEBIT, amount,
                TransactionReferenceType.WITHDRAWAL_REQUEST, requestId);
    }

    public WalletTransaction payment(Long userId, Long orderId, BigDecimal amount) {
        return build(userId, TransactionType.PAYMENT, TransactionDirection.DEBIT, amount,
                TransactionReferenceType.ORDER, orderId);
    }

    public WalletTransaction refund(Long userId, Long orderId, BigDecimal amount) {
        return build(userId, TransactionType.REFUND, TransactionDirection.CREDIT, amount,
                TransactionReferenceType.ORDER, orderId);
    }

    private WalletTransaction build(
            Long userId,
            TransactionType type,
            TransactionDirection direction,
            BigDecimal amount,
            TransactionReferenceType referenceType,
            Long referenceId
    ) {
        return WalletTransaction.builder()
                .userId(userId)
                .type(type)
                .direction(direction)
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .refType(referenceType)
                .refId(referenceId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
