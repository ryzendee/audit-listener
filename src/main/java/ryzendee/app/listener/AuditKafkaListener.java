package ryzendee.app.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ryzendee.app.model.AuditRecord;
import ryzendee.app.repository.AuditRecordRepository;
import ryzendee.starter.audit.model.*;

/***
 * Слушатель Kafka для обработки аудиторских записей из Kafka-топика
 *
 * Обрабатывает различные типы аудиторских записей и сохраняет их в базу данных
 *
 * @author Dmitry Ryazantsev
 */

@KafkaListener(topics = "${audit.kafka.listener.topic.name}")
@Component
@Log4j2
@RequiredArgsConstructor
public class AuditKafkaListener {

    private static final String HEADER_MESSAGE_ID = "messageId";

    private final AuditRecordRepository auditRecordRepository;

    /**
     * Обрабатывает аудиторские записи HTTP, полученные из Kafka.
     *
     * @param messageId уникальный идентификатор сообщения из заголовка Kafka
     * @param entry     полезная нагрузка с HTTP-аудиторской записью
     */
    @Transactional
    @KafkaHandler
    public void handleHttpAuditEntry(@Header(HEADER_MESSAGE_ID) String messageId,
                                     @Payload HttpAuditLogEntry entry) {
        AuditRecord entity = create(messageId, entry);
        save(entity);
    }

    /**
     * Обрабатывает аудиторские записи методов, полученные из Kafka.
     *
     * @param messageId уникальный идентификатор сообщения из заголовка Kafka
     * @param entry     полезная нагрузка с аудиторской записью метода
     */
    @Transactional
    @KafkaHandler
    public void handleMethodAuditEntry(@Header(HEADER_MESSAGE_ID) String messageId,
                                       @Payload MethodAuditLogEntry entry) {
        AuditRecord entity = create(messageId, entry);
        save(entity);
    }

    private AuditRecord create(String messageId, AuditLogEntry entry) {
        return new AuditRecord(messageId, entry);
    }

    private void save(AuditRecord auditRecord) {
        try {
            auditRecordRepository.saveAndFlush(auditRecord);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Entity with given message id already exists: {}", auditRecord.getMessageId());
        }
    }
}
