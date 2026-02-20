package id.ac.ui.cs.a04.json.wallet.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class WithdrawalRequest {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String destination;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}
