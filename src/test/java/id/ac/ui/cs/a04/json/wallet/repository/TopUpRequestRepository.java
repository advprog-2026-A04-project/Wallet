package id.ac.ui.cs.a04.json.wallet.repository;

import id.ac.ui.cs.a04.json.wallet.model.TopUpRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopUpRequestRepository extends JpaRepository<TopUpRequest, Long> {
}
