package id.ac.ui.cs.a04.json.wallet.repository;

import id.ac.ui.cs.a04.json.wallet.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
