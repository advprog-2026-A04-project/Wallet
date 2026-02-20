package id.ac.ui.cs.a04.json.wallet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Wallet {
    @Id
    private Long userId;
    private BigDecimal balance;

    public BigDecimal decreaseBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Balance decrease amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Balance decrease amount cannot be greater than current balance");
        }
        balance = balance.subtract(amount);
        return balance;
    }

    public BigDecimal increaseBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Balance increase amount must be positive");
        }
        balance = balance.add(amount);
        return balance;
    }
}
