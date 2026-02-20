package id.ac.ui.cs.a04.json.wallet.dto;

import id.ac.ui.cs.a04.json.wallet.model.TransactionDirection;
import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionStatus;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionDTO {
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
