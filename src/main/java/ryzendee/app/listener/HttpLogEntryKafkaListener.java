package ryzendee.app.listener;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ryzendee.app.mapper.HttpLogEntryDocumentMapper;
import ryzendee.app.model.HttpLogDocument;
import ryzendee.app.repository.AuditRecordRepository;
import ryzendee.app.repository.HttpLogDocumentRepository;
import ryzendee.starter.audit.model.HttpAuditLogEntry;

@KafkaListener(topics = "${audit.kafka.listener.topic.requests.name}")
@Component
public class HttpLogEntryKafkaListener extends AbstractAuditKafkaListener<HttpAuditLogEntry> {

    private static final String HEADER_MESSAGE_ID = "messageId";

    private final HttpLogEntryDocumentMapper httpLogEntryDocumentMapper;
    private final HttpLogDocumentRepository httpLogDocumentRepository;

    public HttpLogEntryKafkaListener(AuditRecordRepository auditRecordRepository, HttpLogEntryDocumentMapper httpLogEntryDocumentMapper, HttpLogDocumentRepository httpLogDocumentRepository) {
        super(auditRecordRepository);
        this.httpLogEntryDocumentMapper = httpLogEntryDocumentMapper;
        this.httpLogDocumentRepository = httpLogDocumentRepository;
    }

    @Transactional
    @KafkaHandler
    public void handleHttpAuditEntry(@Header(HEADER_MESSAGE_ID) String messageId,
                                     @Payload HttpAuditLogEntry entry) {
        super.handleMessage(messageId, entry);
    }

    @Override
    protected void process(HttpAuditLogEntry entry, String messageId) {
        HttpLogDocument document = httpLogEntryDocumentMapper.toDocument(entry);
        httpLogDocumentRepository.save(document);
    }
}
