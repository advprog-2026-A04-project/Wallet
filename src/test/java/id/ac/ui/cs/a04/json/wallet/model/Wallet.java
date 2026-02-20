package id.ac.ui.cs.a04.json.wallet.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Wallet {
    private Long userId;
    private BigDecimal balance;
}
