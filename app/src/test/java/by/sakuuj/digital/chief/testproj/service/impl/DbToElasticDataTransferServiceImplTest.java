package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.dto.ProductDocumentDto;
import by.sakuuj.digital.chief.testproj.dto.SkuDocumentDto;
import by.sakuuj.digital.chief.testproj.service.IndexCreationService;
import by.sakuuj.digital.chief.testproj.utils.Base64Utils;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class DbToElasticDataTransferServiceImplTest {

    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String DATABASE = "postgres";

    private static final String ELASTICSEARCH_USERNAME = "elastic";
    private static final String ELASTICSEARCH_PASSWORD = "elastic1dfkdfjePASS";

    private static final String INDEX_FILE_NAME = "main-index.json";
    private static final String INDEX_BASE_PATH = "elasticsearch/index/";
    private static final String INDEX_NAME = "my-index";

    @Container
    private static final GenericContainer<?> postgresContainer = new GenericContainer<>("postgres:16.4")
            .withEnv("POSTGRES_PASSWORD", PASSWORD)
            .withEnv("POSTGRES_USER", USERNAME)
            .withEnv("POSTGRES_DB", DATABASE)
            .withExposedPorts(5432);

    @Container
    private static final GenericContainer<?> elasticsearchContainer = new GenericContainer<>("elasticsearch:8.15.3")
            .withEnv("ELASTIC_PASSWORD", ELASTICSEARCH_PASSWORD)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.http.ssl.enabled", "false")
            .withExposedPorts(9200);


    private static String getPostgresJdbcUrl() {
        return "jdbc:postgresql://" + postgresContainer.getHost() + ":"
                + postgresContainer.getFirstMappedPort()
                + "/" + DATABASE;
    }

    @DynamicPropertySource
    static void setPostgresProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> List.of(getPostgresJdbcUrl()));
        registry.add("spring.datasource.username", () -> USERNAME);
        registry.add("spring.datasource.password", () -> PASSWORD);
    }

    @DynamicPropertySource
    static void setElasticsearchProps(DynamicPropertyRegistry registry) {
        registry.add("by.sakuuj.elasticsearch.server.url", DbToElasticDataTransferServiceImplTest::getElasticsearchUrl);
        registry.add("by.sakuuj.elasticsearch.password", () -> ELASTICSEARCH_PASSWORD);
        registry.add("by.sakuuj.elasticsearch.json-files.index.base-path", () -> INDEX_BASE_PATH);
        registry.add("by.sakuuj.elasticsearch.main-index.name", () -> INDEX_NAME);
    }

    protected static String getBasicAuthHeaderValue() {
        return "Basic " + Base64Utils.encode(ELASTICSEARCH_USERNAME + ":" + ELASTICSEARCH_PASSWORD);
    }

    protected static String getElasticsearchUrl() {

        return "http://%s:%s".formatted(
                elasticsearchContainer.getHost(), elasticsearchContainer.getFirstMappedPort().toString());
    }

    @Autowired
    private DbToElasticDataTransferServiceImpl serviceImpl;

    @Autowired
    private IndexCreationService indexCreationService;

    @Autowired
    private ElasticsearchClient esClient;

    @BeforeEach
    void createIndex() {

        indexCreationService.createIndex(INDEX_NAME, INDEX_FILE_NAME);
    }

    @AfterEach
    void removeIndex() throws IOException {

        esClient.indices().delete(b -> b.index(INDEX_NAME));
    }

    @Test
    void shouldTransferProducts() throws IOException {
        // given
        LocalDateTime pointInTime = LocalDateTime.parse("2024-10-07T16:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        serviceImpl.transferProductsCreatedAtAfterPointInTime(pointInTime);

        esClient.indices().refresh();

        // when
        SearchResponse<ProductDocumentDto> found = esClient.search(b -> b.size(100).index(INDEX_NAME)
                        .query(
                                b1 -> b1.term(
                                        b2 -> b2.field("join_field").value("product")
                                )
                        ),
                ProductDocumentDto.class);
        List<Hit<ProductDocumentDto>> hits = found.hits().hits();

        // then
        assertThat(hits).hasSize(13);
    }

    @Test
    void shouldTransferSkus() throws IOException {
        // given
        LocalDateTime pointInTime = LocalDateTime.parse("2024-10-21T14:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        serviceImpl.transferSkusCreatedAtAfterPointInTime(pointInTime);

        esClient.indices().refresh();

        // when
        SearchResponse<SkuDocumentDto> found = esClient.search(b -> b.size(100).index(INDEX_NAME)
                        .query(
                                b1 -> b1.term(
                                        b2 -> b2.field("join_field").value("sku")
                                )
                        ),
                SkuDocumentDto.class);
        List<Hit<SkuDocumentDto>> hits = found.hits().hits();

        // then
        assertThat(hits).hasSize(45);
    }

}