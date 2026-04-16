package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.model.TransactionDirection;
import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WalletTransactionFactoryTest {

    private final WalletTransactionFactory factory = new WalletTransactionFactory();

    @Test
    void shouldBuildTopUpTransaction() {
        WalletTransaction transaction = factory.topUpSuccess(1L, 2L, new BigDecimal("10000"));

        assertTransaction(transaction, TransactionType.TOPUP, TransactionDirection.CREDIT, TransactionReferenceType.TOPUP_REQUEST, 2L);
    }

    @Test
    void shouldBuildWithdrawalTransaction() {
        WalletTransaction transaction = factory.withdrawSuccess(1L, 3L, new BigDecimal("5000"));

        assertTransaction(transaction, TransactionType.WITHDRAWAL, TransactionDirection.DEBIT, TransactionReferenceType.WITHDRAWAL_REQUEST, 3L);
    }

    @Test
    void shouldBuildPaymentTransaction() {
        WalletTransaction transaction = factory.payment(1L, 4L, new BigDecimal("2500"));

        assertTransaction(transaction, TransactionType.PAYMENT, TransactionDirection.DEBIT, TransactionReferenceType.ORDER, 4L);
    }

    @Test
    void shouldBuildRefundTransaction() {
        WalletTransaction transaction = factory.refund(1L, 5L, new BigDecimal("2500"));

        assertTransaction(transaction, TransactionType.REFUND, TransactionDirection.CREDIT, TransactionReferenceType.ORDER, 5L);
    }

    private static void assertTransaction(
            WalletTransaction transaction,
            TransactionType type,
            TransactionDirection direction,
            TransactionReferenceType referenceType,
            Long referenceId
    ) {
        assertEquals(1L, transaction.getUserId());
        assertEquals(type, transaction.getType());
        assertEquals(direction, transaction.getDirection());
        assertEquals(referenceType, transaction.getRefType());
        assertEquals(referenceId, transaction.getRefId());
        assertNotNull(transaction.getCreatedAt());
    }
}
