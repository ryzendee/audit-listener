package ryzendee.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ryzendee.starter.audit.model.AuditLogEntry;

/***
 * Сущность для сохранения информации об уже обработанных записей аудита
 *
 * @author Dmitry Ryazantsev
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageId;

    public AuditRecord(String messageId) {
        this.messageId = messageId;
    }
}
