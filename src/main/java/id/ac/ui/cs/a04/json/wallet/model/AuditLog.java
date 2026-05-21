package id.ac.ui.cs.a04.json.wallet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private AuditLogAction action;

    private String actorType;
    private Long actorId;

    private String resourceType;
    private Long resourceId;

    private String ipAddress;

    private boolean success;
    private String note;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
}
