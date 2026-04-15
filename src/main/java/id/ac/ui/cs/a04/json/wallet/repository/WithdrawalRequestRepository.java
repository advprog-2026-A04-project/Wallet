package id.ac.ui.cs.a04.json.wallet.repository;

import id.ac.ui.cs.a04.json.wallet.model.WithdrawalRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    WithdrawalRequest findWithdrawalRequestById(Long id);
}
