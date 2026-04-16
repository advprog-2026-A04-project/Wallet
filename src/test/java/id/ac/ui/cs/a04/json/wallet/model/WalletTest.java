package id.ac.ui.cs.a04.json.wallet.model;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletTest {

    @Test
    void decreaseBalanceShouldSubtractAmount() {
        Wallet wallet = new Wallet(7L, new BigDecimal("150000"));

        BigDecimal balance = wallet.decreaseBalance(new BigDecimal("50000"));

        assertEquals(new BigDecimal("100000"), balance);
        assertEquals(new BigDecimal("100000"), wallet.getBalance());
    }

    @Test
    void decreaseBalanceShouldRejectNonPositiveAmount() {
        Wallet wallet = new Wallet(7L, new BigDecimal("150000"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wallet.decreaseBalance(BigDecimal.ZERO)
        );

        assertEquals("Balance decrease amount must be positive", exception.getMessage());
    }

    @Test
    void decreaseBalanceShouldRejectAmountGreaterThanBalance() {
        Wallet wallet = new Wallet(7L, new BigDecimal("150000"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wallet.decreaseBalance(new BigDecimal("200000"))
        );

        assertEquals("Balance decrease amount cannot be greater than current balance", exception.getMessage());
    }

    @Test
    void increaseBalanceShouldAddAmount() {
        Wallet wallet = new Wallet(7L, new BigDecimal("150000"));

        BigDecimal balance = wallet.increaseBalance(new BigDecimal("50000"));

        assertEquals(new BigDecimal("200000"), balance);
        assertEquals(new BigDecimal("200000"), wallet.getBalance());
    }

    @Test
    void increaseBalanceShouldRejectNonPositiveAmount() {
        Wallet wallet = new Wallet(7L, new BigDecimal("150000"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wallet.increaseBalance(new BigDecimal("-1"))
        );

        assertEquals("Balance increase amount must be positive", exception.getMessage());
    }
}
