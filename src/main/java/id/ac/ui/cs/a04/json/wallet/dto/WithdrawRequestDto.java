package id.ac.ui.cs.a04.json.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record WithdrawRequestDto(
        @NotNull Long userId,
        @NotNull @DecimalMin(value = "1.00") BigDecimal amount,
        @NotBlank String destination
) {
}
