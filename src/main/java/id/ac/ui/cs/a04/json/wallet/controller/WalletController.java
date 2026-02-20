package id.ac.ui.cs.a04.json.wallet.controller;

import id.ac.ui.cs.a04.json.wallet.model.TransactionDirection;
import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionStatus;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import id.ac.ui.cs.a04.json.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@RequestBody Long userId) {
        // TODO: Authentication
        BigDecimal balance = walletService.getWalletByUserId(userId).getBalance();
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions() {
        // TODO: Authentication
        List<WalletTransaction> list = walletService.getAllTransactions();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/topup")
    public ResponseEntity<Boolean> topUp(@RequestBody BigDecimal amount)  {
        // TODO: Functionality
        // TODO: Authentication
        return ResponseEntity.ok(true);
    }

    @PostMapping("/topup/{id}/mark-success")
    public ResponseEntity<Boolean> topUpMarkSuccess() {
        // TODO: Functionality
        // TODO: Authentication
        return ResponseEntity.ok(true);
    }

    @PostMapping("/topup/{id}/mark-failed")
    public ResponseEntity<Boolean> topUpMarkFailed() {
        // TODO: Functionality
        // TODO: Authentication
        return ResponseEntity.ok(true);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Boolean> withdraw(@RequestBody BigDecimal amount, @RequestBody String destination)  {
        // TODO: Functionality
        // TODO: Authentication
        return ResponseEntity.ok(true);
    }

    @PostMapping("/withdraw/{id}/mark-success")
    public ResponseEntity<Boolean> withdrawMarkSuccess() {
        // TODO: Functionality
        // TODO: Authentication
        return ResponseEntity.ok(true);
    }

    @PostMapping("/withdraw/{id}/mark-failed")
    public ResponseEntity<Boolean> withdrawMarkFailed() {
        // TODO: Functionality
        // TODO: Authentication
        return ResponseEntity.ok(true);
    }

    @PostMapping("/deduct")
    public ResponseEntity<BigDecimal> deduct(@RequestBody Long userId, @RequestBody BigDecimal amount, @RequestBody Long orderId) {
        // TODO: Authentication
        BigDecimal newBalance = walletService.deduct(userId, orderId, amount);
        return ResponseEntity.ok(newBalance);
    }

    @PostMapping("/refund")
    public ResponseEntity<BigDecimal> refund(@RequestBody Long userId, @RequestBody BigDecimal amount, @RequestBody Long orderId) {
        // TODO: Authentication
        BigDecimal newBalance = walletService.refund(userId, orderId, amount);
        return ResponseEntity.ok(newBalance);
    }

}
