package id.ac.ui.cs.a04.json.wallet.repository;

import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import id.ac.ui.cs.a04.json.wallet.model.TransactionReferenceType;
import id.ac.ui.cs.a04.json.wallet.model.TransactionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Optional<WalletTransaction> findByUserIdAndTypeAndRefTypeAndRefId(
            Long userId,
            TransactionType type,
            TransactionReferenceType refType,
            Long refId
    );

    List<WalletTransaction> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
