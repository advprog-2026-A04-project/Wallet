package id.ac.ui.cs.a04.json.wallet.dto;

import jakarta.validation.constraints.NotNull;

public record UserIdRequest(@NotNull Long userId) {
}
