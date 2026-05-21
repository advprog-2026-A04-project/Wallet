package id.ac.ui.cs.a04.json.wallet.service;

import id.ac.ui.cs.a04.json.wallet.model.AuditLog;
import id.ac.ui.cs.a04.json.wallet.repository.AuditLogRepository;
import org.springframework.transaction.annotation.Transactional;

public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void record(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
}
