package ryzendee.app.listener;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ryzendee.app.mapper.HttpLogEntryDocumentMapper;
import ryzendee.app.model.AuditRecord;
import ryzendee.app.model.HttpLogDocument;
import ryzendee.app.repository.AuditRecordRepository;
import ryzendee.app.repository.HttpLogDocumentRepository;
import ryzendee.starter.audit.model.HttpAuditLogEntry;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import({KafkaListenerTestConfig.class, KafkaAutoConfiguration.class})
@EmbeddedKafka(
        controlledShutdown = true,
        bootstrapServersProperty = "spring.kafka.consumer.bootstrap-servers",
        kraft = true
)
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {HttpLogEntryKafkaListener.class})
public class HttpLogEntryKafkaListenerIT {

    private static final int VERIFY_TIMEOUT = 15000;

    @MockitoSpyBean
    public HttpLogEntryKafkaListener httpLogEntryKafkaListener;

    @MockitoBean
    public AuditRecordRepository auditRecordRepository;
    @MockitoBean
    public HttpLogEntryDocumentMapper httpLogEntryDocumentMapper;
    @MockitoBean
    private HttpLogDocumentRepository httpLogDocumentRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${audit.kafka.listener.topic.requests.name}")
    private String auditRequestsTopic;

    @Test
    void handleHttpAuditEntry_shouldSaveEntry() throws Exception {
        // Arrange
        ProducerRecord<String, Object> record = createProducerRecord(auditRequestsTopic, new HttpAuditLogEntry());
        ArgumentCaptor<String> headerMessageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpAuditLogEntry> payloadCaptor = ArgumentCaptor.forClass(HttpAuditLogEntry.class);
        when(httpLogEntryDocumentMapper.toDocument(any(HttpAuditLogEntry.class)))
                .thenReturn(new HttpLogDocument());

        // Act
        kafkaTemplate.send(record).get();
        verify(httpLogEntryKafkaListener, timeout(VERIFY_TIMEOUT))
                .handleHttpAuditEntry(headerMessageIdCaptor.capture(), payloadCaptor.capture());

        // Assert
        verify(httpLogEntryDocumentMapper).toDocument(any(HttpAuditLogEntry.class));
        verify(httpLogDocumentRepository).save(any(HttpLogDocument.class));
        verify(auditRecordRepository).saveAndFlush(any(AuditRecord.class));
        assertThat(headerMessageIdCaptor.getValue()).isNotNull();
        assertThat(payloadCaptor.getValue()).isNotNull();
    }

    private ProducerRecord<String, Object> createProducerRecord(String topic, Object obj) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, obj);
        record.headers().add("messageId", randomUUID().toString().getBytes());
        return record;
    }
}
