package ryzendee.app;

import org.springframework.boot.SpringApplication;

public class TestAuditListenerApplication {

    public static void main(String[] args) {
        SpringApplication.from(AuditListenerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
