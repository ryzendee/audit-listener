package ryzendee.app.listener;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ryzendee.app.mapper.MethodLogDocumentMapper;
import ryzendee.app.model.MethodLogDocument;
import ryzendee.app.repository.AuditRecordRepository;
import ryzendee.app.repository.MethodLogDocumentRepository;
import ryzendee.starter.audit.model.MethodAuditLogEntry;

@KafkaListener(topics = "${audit.kafka.listener.topic.methods.name}")
@Component
public class MethodLogEntryKafkaListener extends AbstractAuditKafkaListener<MethodAuditLogEntry> {

    private static final String HEADER_MESSAGE_ID = "messageId";

    private final MethodLogDocumentMapper methodLogDocumentMapper;
    private final MethodLogDocumentRepository methodLogDocumentRepository;

    public MethodLogEntryKafkaListener(AuditRecordRepository auditRecordRepository, MethodLogDocumentMapper methodLogDocumentMapper, MethodLogDocumentRepository methodLogDocumentRepository) {
        super(auditRecordRepository);
        this.methodLogDocumentMapper = methodLogDocumentMapper;
        this.methodLogDocumentRepository = methodLogDocumentRepository;
    }

    @Transactional
    @KafkaHandler
    public void handleMethodAuditEntry(@Header(HEADER_MESSAGE_ID) String messageId,
                                       @Payload MethodAuditLogEntry entry) {
        super.handleMessage(messageId, entry);
    }

    @Override
    protected void process(MethodAuditLogEntry entry, String messageId) {
        MethodLogDocument document = methodLogDocumentMapper.toDocument(entry);
        methodLogDocumentRepository.save(document);
    }
}
