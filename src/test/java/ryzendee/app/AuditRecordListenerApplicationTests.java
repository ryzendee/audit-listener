package ryzendee.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
class AuditRecordListenerApplicationTests extends AbstractTestcontainers {

    @BeforeAll
    static void startContainers() {
        postgreSQLContainer.start();
        elasticsearchContainer.start();
    }

    @Test
    void contextLoads() {
    }

}
