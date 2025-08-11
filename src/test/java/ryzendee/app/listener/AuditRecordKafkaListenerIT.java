package ryzendee.app.listener;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
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
import org.testcontainers.containers.PostgreSQLContainer;
import ryzendee.app.model.AuditRecord;
import ryzendee.app.repository.AuditRecordRepository;
import ryzendee.starter.audit.model.*;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Import({AuditRecordKafkaListenerTestConfig.class, KafkaAutoConfiguration.class})
@EmbeddedKafka(
        controlledShutdown = true,
        bootstrapServersProperty = "spring.kafka.consumer.bootstrap-servers",
        kraft = true
)
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {AuditKafkaListener.class})
public class AuditRecordKafkaListenerIT {

    private static final int VERIFY_TIMEOUT = 15000;

    @MockitoSpyBean
    public AuditKafkaListener auditKafkaListener;

    @MockitoBean
    public AuditRecordRepository auditRecordRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${audit.kafka.listener.topic.name}")
    private String auditTopic;

    @Test
    void handleHttpAuditEntry_shouldSaveEntry() throws Exception {
        ProducerRecord<String, Object> record = createProducerRecord(new HttpAuditLogEntry());
        ArgumentCaptor<String> headerMessageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpAuditLogEntry> payloadCaptor = ArgumentCaptor.forClass(HttpAuditLogEntry.class);

        kafkaTemplate.send(record).get();

        verify(auditKafkaListener, timeout(VERIFY_TIMEOUT))
                .handleHttpAuditEntry(headerMessageIdCaptor.capture(), payloadCaptor.capture());
        verify(auditRecordRepository).saveAndFlush(any(AuditRecord.class));
        assertThat(headerMessageIdCaptor.getValue()).isNotNull();
        assertThat(payloadCaptor.getValue()).isNotNull();
    }

    @Test
    void handleMethodAuditEntry_shouldSaveEntry() throws Exception {
        ProducerRecord<String, Object> record = createProducerRecord(new MethodAuditLogEntry());
        ArgumentCaptor<String> headerMessageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MethodAuditLogEntry> payloadCaptor = ArgumentCaptor.forClass(MethodAuditLogEntry.class);

        kafkaTemplate.send(record).get();

        verify(auditKafkaListener, timeout(VERIFY_TIMEOUT))
                .handleMethodAuditEntry(headerMessageIdCaptor.capture(), payloadCaptor.capture());
        verify(auditRecordRepository).saveAndFlush(any(AuditRecord.class));
        assertThat(headerMessageIdCaptor.getValue()).isNotNull();
        assertThat(payloadCaptor.getValue()).isNotNull();
    }

    private ProducerRecord<String, Object> createProducerRecord(Object obj) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(auditTopic, obj);
        record.headers().add("messageId", randomUUID().toString().getBytes());
        return record;
    }
}
