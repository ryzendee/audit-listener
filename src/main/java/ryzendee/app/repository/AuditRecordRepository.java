package ryzendee.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ryzendee.app.model.AuditRecord;

/***
 * Репозиторий для работы с сущностью записи аудита {@link AuditRecord}
 *
 * @author Dmitry Ryazantsev
 */
public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {
}
