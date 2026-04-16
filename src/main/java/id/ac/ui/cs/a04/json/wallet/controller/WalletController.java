package id.ac.ui.cs.a04.json.wallet.controller;

import id.ac.ui.cs.a04.json.wallet.dto.OrderAmountRequest;
import id.ac.ui.cs.a04.json.wallet.dto.RequestStatusResponse;
import id.ac.ui.cs.a04.json.wallet.dto.TopUpRequestDto;
import id.ac.ui.cs.a04.json.wallet.dto.UserIdRequest;
import id.ac.ui.cs.a04.json.wallet.dto.WalletBalanceResponse;
import id.ac.ui.cs.a04.json.wallet.dto.WithdrawRequestDto;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import id.ac.ui.cs.a04.json.wallet.service.WalletService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletAccessGuard walletAccessGuard;

    public WalletController(WalletService walletService, WalletAccessGuard walletAccessGuard) {
        this.walletService = walletService;
        this.walletAccessGuard = walletAccessGuard;
    }

    @PostMapping("/balance")
    public ResponseEntity<WalletBalanceResponse> getBalance(
            Authentication authentication,
            @Valid @RequestBody UserIdRequest request
    ) {
        walletAccessGuard.requireUserAccess(authentication, request.userId(), true);
        return ResponseEntity.ok(walletService.getBalance(request.userId()));
    }

    @PostMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(
            Authentication authentication,
            @Valid @RequestBody UserIdRequest request
    ) {
        walletAccessGuard.requireUserAccess(authentication, request.userId(), true);
        return ResponseEntity.ok(walletService.getTransactions(request.userId()));
    }

    @PostMapping("/topup")
    public ResponseEntity<RequestStatusResponse> topUp(
            Authentication authentication,
            @Valid @RequestBody TopUpRequestDto request
    ) {
        walletAccessGuard.requireUserAccess(authentication, request.userId(), false);
        Long topUpId = walletService.createTopUpRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RequestStatusResponse(topUpId, true));
    }

    @PostMapping("/topup/{id}/mark-success")
    public ResponseEntity<RequestStatusResponse> topUpMarkSuccess(@PathVariable Long id) {
        return ResponseEntity.ok(new RequestStatusResponse(id, walletService.markTopUpSuccess(id)));
    }

    @PostMapping("/topup/{id}/mark-failed")
    public ResponseEntity<RequestStatusResponse> topUpMarkFailed(@PathVariable Long id) {
        return ResponseEntity.ok(new RequestStatusResponse(id, walletService.markTopUpFailed(id)));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<RequestStatusResponse> withdraw(
            Authentication authentication,
            @Valid @RequestBody WithdrawRequestDto request
    ) {
        walletAccessGuard.requireUserAccess(authentication, request.userId(), false);
        Long withdrawalId = walletService.createWithdrawRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RequestStatusResponse(withdrawalId, true));
    }

    @PostMapping("/withdraw/{id}/mark-success")
    public ResponseEntity<RequestStatusResponse> withdrawMarkSuccess(@PathVariable Long id) {
        return ResponseEntity.ok(new RequestStatusResponse(id, walletService.markWithdrawSuccess(id)));
    }

    @PostMapping("/withdraw/{id}/mark-failed")
    public ResponseEntity<RequestStatusResponse> withdrawMarkFailed(@PathVariable Long id) {
        return ResponseEntity.ok(new RequestStatusResponse(id, walletService.markWithdrawFailed(id)));
    }

    @PostMapping("/deduct")
    public ResponseEntity<WalletBalanceResponse> deduct(
            Authentication authentication,
            @Valid @RequestBody OrderAmountRequest request
    ) {
        walletAccessGuard.requireUserAccess(authentication, request.userId(), true);
        return ResponseEntity.ok(walletService.deduct(request.userId(), request.orderId(), request.amount()));
    }

    @PostMapping("/refund")
    public ResponseEntity<WalletBalanceResponse> refund(
            Authentication authentication,
            @Valid @RequestBody OrderAmountRequest request
    ) {
        walletAccessGuard.requireUserAccess(authentication, request.userId(), true);
        return ResponseEntity.ok(walletService.refund(request.userId(), request.orderId(), request.amount()));
    }
}
