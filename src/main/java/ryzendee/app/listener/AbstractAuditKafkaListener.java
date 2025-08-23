package ryzendee.app.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import ryzendee.app.model.AuditRecord;
import ryzendee.app.repository.AuditRecordRepository;
import ryzendee.starter.audit.model.AuditLogEntry;

/**
 * Абстрактный слушатель Kafka для базовой обработки сообщений аудита.
 * <p>
 * Этот класс обеспечивает сохранение записи аудита в базу данных
 * и обработку сообщения с помощью метода {@link #process(AuditLogEntry, String)}.
 *
 * @param <E> тип записи аудита, наследник {@link AuditLogEntry}
 *
 * @author Dmitry Ryazantsev
 */
@Log4j2
@RequiredArgsConstructor
public abstract class AbstractAuditKafkaListener<E extends AuditLogEntry> {

    protected final AuditRecordRepository auditRecordRepository;

    /**
     * Обрабатывает входящее сообщение:
     * сохраняет запись аудита и вызывает {@link #process(AuditLogEntry, String)}.
     * <p>
     * Если запись уже существует, логируется предупреждение.
     * При других ошибках выбрасывает исключение.
     *
     * @param messageId уникальный идентификатор сообщения
     * @param entry     запись аудита
     */
    protected void handleMessage(String messageId, E entry) {
        try {
            AuditRecord record = new AuditRecord(messageId);
            auditRecordRepository.saveAndFlush(record);
            process(entry, messageId);
            log.info("MessageId={} processed and stored", messageId);
        } catch (DataIntegrityViolationException ex) {
            log.warn("MessageId={} already processed", messageId);
        } catch (Exception ex) {
            log.error("MessageId={} failed to process", messageId, ex);
            throw ex;
        }
    }

    /**
     * Обрабатывает запись аудита с конкретной бизнес-логикой.
     *
     * @param entry     запись аудита
     * @param messageId идентификатор сообщения
     */
    protected abstract void process(E entry, String messageId);

}
