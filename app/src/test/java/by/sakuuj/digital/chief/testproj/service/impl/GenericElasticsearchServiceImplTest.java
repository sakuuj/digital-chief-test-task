package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.ProductDocumentTestBuilder;
import by.sakuuj.digital.chief.testproj.SkuDocumentTestBuilder;
import by.sakuuj.digital.chief.testproj.configs.ElasticsearchClientConfig;
import by.sakuuj.digital.chief.testproj.configs.JacksonConfig;
import by.sakuuj.digital.chief.testproj.dto.ProductDocumentDto;
import by.sakuuj.digital.chief.testproj.dto.SkuDocumentDto;
import by.sakuuj.digital.chief.testproj.service.IndexCreationService;
import by.sakuuj.digital.chief.testproj.testconfigs.EmptyConfig;
import by.sakuuj.digital.chief.testproj.testconfigs.RestClientTestConfig;
import by.sakuuj.digital.chief.testproj.testcontainers.ElasticsearchSingletonContainerLauncher;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Import({
        JacksonConfig.class,
        ElasticsearchClientConfig.class,
        GenericElasticsearchServiceImpl.class,
        IndexCreationServiceImpl.class
})
@SpringJUnitConfig(classes = EmptyConfig.class)
class GenericElasticsearchServiceImplTest extends ElasticsearchSingletonContainerLauncher {

    @Autowired
    private GenericElasticsearchServiceImpl serviceImpl;

    @Autowired
    private IndexCreationService indexCreationService;

    @Autowired
    private ElasticsearchClient esClient;

    private static final String INDEX_FILE_NAME = "main-index.json";
    private static final String INDEX_BASE_PATH = "elasticsearch/index/";
    private static final String INDEX_NAME = "my-index";

    @DynamicPropertySource
    static void setAdditionalDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("by.sakuuj.elasticsearch.json-files.index.base-path", () -> INDEX_BASE_PATH);
    }

    @BeforeEach
    void createIndex() {

        indexCreationService.createIndex(INDEX_NAME, INDEX_FILE_NAME);
    }

    @AfterEach
    void removeIndex() throws IOException {

        esClient.indices().delete(b -> b.index(INDEX_NAME));
    }

    @Test
    void shouldIndexInBulkForJoinParent() throws IOException, ExecutionException, InterruptedException {


        ProductDocumentDto firstDto = ProductDocumentTestBuilder.builder().buildDto();
        ProductDocumentDto secondDto = ProductDocumentTestBuilder.builder()
                .withId(UUID.fromString("46f4e776-888e-4cb8-b370-17282f5b2985"))
                .buildDto();

        serviceImpl.indexInBulk(List.of(firstDto, secondDto), dto -> dto.id().toString(), INDEX_NAME).get();

        esClient.indices().refresh();

        SearchResponse<ProductDocumentDto> searchResponse = esClient.search(
                b0 -> b0.index(INDEX_NAME),
                ProductDocumentDto.class
        );

        Hit<ProductDocumentDto> firstHit = searchResponse.hits().hits().getFirst();
        ProductDocumentDto actualFirstDto = firstHit.source().withId(UUID.fromString(firstHit.id()));

        Hit<ProductDocumentDto> lastHit = searchResponse.hits().hits().getLast();
        ProductDocumentDto actualLastDto = lastHit.source().withId(UUID.fromString(lastHit.id()));

        Assertions.assertThat(List.of(actualFirstDto, actualLastDto))
                .containsExactlyInAnyOrder(firstDto, secondDto);
    }

    @Test
    void shouldIndexInBulkForJoinChild() throws IOException, ExecutionException, InterruptedException {


        SkuDocumentDto firstDto = SkuDocumentTestBuilder.builder().buildDto();
        SkuDocumentDto secondDto = SkuDocumentTestBuilder.builder()
                .withId(UUID.fromString("46f4e776-888e-4cb8-b370-17282f5b2985"))
                .buildDto();

        serviceImpl.indexInBulk(List.of(firstDto, secondDto), dto -> dto.getId().toString(), INDEX_NAME).get();

        esClient.indices().refresh();

        SearchResponse<SkuDocumentDto> searchResponse = esClient.search(
                b0 -> b0.index(INDEX_NAME),
                SkuDocumentDto.class
        );

        Hit<SkuDocumentDto> firstHit = searchResponse.hits().hits().getFirst();
        SkuDocumentDto actualFirstDto = firstHit.source().setId(UUID.fromString(firstHit.id()));

        Hit<SkuDocumentDto> lastHit = searchResponse.hits().hits().getLast();
        SkuDocumentDto actualLastDto = lastHit.source().setId(UUID.fromString(lastHit.id()));

        Assertions.assertThat(List.of(actualFirstDto, actualLastDto))
                .containsExactlyInAnyOrder(firstDto, secondDto);
    }
}

