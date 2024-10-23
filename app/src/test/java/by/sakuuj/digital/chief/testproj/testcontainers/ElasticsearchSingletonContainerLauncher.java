package by.sakuuj.digital.chief.testproj.testcontainers;

import by.sakuuj.digital.chief.testproj.utils.Base64Utils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public abstract class ElasticsearchSingletonContainerLauncher {

    protected static final String ELASTICSEARCH_USERNAME = "elastic";
    protected static final String ELASTICSEARCH_PASSWORD = "elastic1dfkdfjePASS";
    protected static final GenericContainer<?> ELASTICSEARCH_CONTAINER;



    static {
        ELASTICSEARCH_CONTAINER = new GenericContainer<>("elasticsearch:8.15.3")
                .withEnv("ELASTIC_PASSWORD", ELASTICSEARCH_PASSWORD)
                .withEnv("discovery.type", "single-node")
                .withEnv("xpack.security.http.ssl.enabled", "false")
                .withExposedPorts(9200);

        ELASTICSEARCH_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("by.sakuuj.elasticsearch.server.url", ElasticsearchSingletonContainerLauncher::getContainerUrl);
        registry.add("spring.elasticsearch.username", () -> ELASTICSEARCH_USERNAME);
        registry.add("by.sakuuj.elasticsearch.password", () -> ELASTICSEARCH_PASSWORD);
    }

    protected static String getBasicAuthHeaderValue() {
        return "Basic " + Base64Utils.encode(ELASTICSEARCH_USERNAME + ":" + ELASTICSEARCH_PASSWORD);
    }

    protected static String getContainerUrl() {

        return "http://%s:%s".formatted(
                ELASTICSEARCH_CONTAINER.getHost(), ELASTICSEARCH_CONTAINER.getFirstMappedPort().toString());
    }

}
