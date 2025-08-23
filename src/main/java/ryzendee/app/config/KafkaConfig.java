package ryzendee.app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    
    private static final int REPLICATION_FACTOR = 1;
    private static final int PARTITIONS = 1;

    @Value("${audit.kafka.listener.topic.methods.name}")
    private String auditMethodsTopic;
    @Value("${audit.kafka.listener.topic.requests.name}")
    private String auditRequestsTopic;
    @Value("${audit.kafka.listener.topic.errors.name}")
    private String auditErrorsTopic;
    @Value("${audit.kafka.listener.topic.errors.settings.retry.interval}")
    private long auditErrorsTopicRetryInterval;
    @Value("${audit.kafka.listener.topic.errors.settings.retry.max-attempts}")
    private long auditErrorsTopicRetryMaxAttempts;


    @Bean
    public NewTopic auditMethodsTopic() {
        return TopicBuilder.name(auditMethodsTopic)
                .replicas(REPLICATION_FACTOR)
                .partitions(PARTITIONS)
                .build();
    }
    
    @Bean
    public NewTopic auditRequestsTopic() {
        return TopicBuilder.name(auditRequestsTopic)
                .replicas(REPLICATION_FACTOR)
                .partitions(PARTITIONS)
                .build();
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(auditErrorsTopic, record.partition())
        );

        FixedBackOff backOff
                = new FixedBackOff(auditErrorsTopicRetryInterval, auditErrorsTopicRetryMaxAttempts);

        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
