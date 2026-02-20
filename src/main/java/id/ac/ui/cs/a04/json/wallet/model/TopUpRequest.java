package id.ac.ui.cs.a04.json.wallet.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopUpRequest {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}
