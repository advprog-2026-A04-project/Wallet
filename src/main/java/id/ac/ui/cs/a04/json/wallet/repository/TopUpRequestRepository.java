package id.ac.ui.cs.a04.json.wallet.repository;

import id.ac.ui.cs.a04.json.wallet.model.TopUpRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TopUpRequestRepository extends JpaRepository<TopUpRequest, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    TopUpRequest findTopUpRequestById(Long id);
}
