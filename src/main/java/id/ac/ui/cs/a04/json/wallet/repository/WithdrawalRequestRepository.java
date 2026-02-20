package id.ac.ui.cs.a04.json.wallet.repository;

import id.ac.ui.cs.a04.json.wallet.model.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
}
