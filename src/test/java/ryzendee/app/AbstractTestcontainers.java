package ryzendee.app;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.testcontainers.utility.MountableFile.forHostPath;


public abstract class AbstractTestcontainers {

    private static final String POSTGRES_IMAGE = "postgres:16.9-alpine";
    private static final String ELASTIC_IMAGE = "elasticsearch:8.18.0";

    private static final String DISCOVERY_TYPE = "discovery.type";
    private static final String DISCOVERY_TYPE_SINGLE_NODE = "single-node";
    private static final String XPACK_SECURITY_ENABLED = "xpack.security.enabled";
    private static final int DURATION_OF_MINUTES_TIMEOUT = 5;
    private static final String INIT_SCRIPT_HOST_PATH = "configs/elastic/init.sh";
    private static final String MAPPINGS_HOST_PATH = "configs/elastic/mappings";
    private static final String INIT_SCRIPT_CONTAINER_PATH = "/es-init.sh";
    private static final String MAPPINGS_CONTAINER_PATH = "/mappings";

    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>(POSTGRES_IMAGE);

    protected static final ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer(DockerImageName.parse(ELASTIC_IMAGE))
                    .withEnv(DISCOVERY_TYPE, DISCOVERY_TYPE_SINGLE_NODE)
                    .withEnv(XPACK_SECURITY_ENABLED, Boolean.FALSE.toString())
                    .withStartupTimeout(Duration.ofMinutes(DURATION_OF_MINUTES_TIMEOUT))
                    .withCopyFileToContainer(forHostPath(INIT_SCRIPT_HOST_PATH), INIT_SCRIPT_CONTAINER_PATH)
                    .withCopyFileToContainer(forHostPath(MAPPINGS_HOST_PATH), MAPPINGS_CONTAINER_PATH)
                    .withReuse(false);


    protected static void executeElasticScript() {
        try {
            elasticsearchContainer.execInContainer("bash", INIT_SCRIPT_CONTAINER_PATH);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @DynamicPropertySource
    static void elasticProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

}
