package id.ac.ui.cs.a04.json.wallet.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class WalletTransaction {

    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private TransactionType type;
    private TransactionDirection direction;
    private BigDecimal amount;
    private TransactionStatus status;
    private TransactionReferenceType refType;
    private Long refId;
    private LocalDateTime createdAt;

}
