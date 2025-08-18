package ryzendee.app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    private static final int REPLICATION_FACTOR = 1;
    private static final int PARTITIONS = 1;

    @Value("${audit.kafka.listener.topic.name}")
    private String applicationLogsTopicName;

    @Bean
    public NewTopic applicationLogsTopic() {
        return TopicBuilder.name(applicationLogsTopicName)
                .replicas(REPLICATION_FACTOR)
                .partitions(PARTITIONS)
                .build();
    }
}
