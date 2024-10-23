package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.ProductDocumentTestBuilder;
import by.sakuuj.digital.chief.testproj.SkuDocumentTestBuilder;
import by.sakuuj.digital.chief.testproj.configs.ElasticsearchClientConfig;
import by.sakuuj.digital.chief.testproj.configs.FormatConfig;
import by.sakuuj.digital.chief.testproj.configs.JacksonConfig;
import by.sakuuj.digital.chief.testproj.dto.ProductDocumentDto;
import by.sakuuj.digital.chief.testproj.dto.ProductResponse;
import by.sakuuj.digital.chief.testproj.dto.SkuDocumentDto;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;
import by.sakuuj.digital.chief.testproj.service.IndexCreationService;
import by.sakuuj.digital.chief.testproj.service.SkuElasticsearchService;
import by.sakuuj.digital.chief.testproj.testconfigs.EmptyConfig;
import by.sakuuj.digital.chief.testproj.testcontainers.ElasticsearchSingletonContainerLauncher;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@Import({
        FormatConfig.class,
        JacksonConfig.class,
        ElasticsearchClientConfig.class,
        IndexCreationServiceImpl.class,
        GenericElasticsearchServiceImpl.class,
        ProductElasticsearchServiceImpl.class,
        SkuElasticsearchServiceImpl.class
})
@ComponentScan(basePackages = "by.sakuuj.digital.chief.testproj.mapper")
@SpringJUnitConfig(classes = EmptyConfig.class)
class ProductElasticsearchServiceImplTest extends ElasticsearchSingletonContainerLauncher {

    @Autowired
    private ElasticsearchClient esClient;

    @Autowired
    private ProductElasticsearchServiceImpl serviceImpl;

    @Autowired
    private SkuElasticsearchService skuElasticsearchService;

    @Autowired
    private IndexCreationService indexCreationService;

    private static final String INDEX_FILE_NAME = "main-index.json";
    private static final String INDEX_BASE_PATH = "elasticsearch/index/";
    private static final String INDEX_NAME = "my-index";

    @DynamicPropertySource
    static void setAdditionalDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("by.sakuuj.elasticsearch.json-files.index.base-path", () -> INDEX_BASE_PATH);
        registry.add("by.sakuuj.elasticsearch.main-index.name", () -> INDEX_NAME);
    }

    private static ProductDocumentDto mapFromProductResponse(ProductResponse response) {

        return ProductDocumentDto.builder()
                .id(response.id())
                .title(response.title())
                .description(response.description())
                .type(response.type())
                .brand(response.brand())
                .updatedAt(response.updatedAt())
                .createdAt(response.createdAt())
                .version(response.version())
                .build();
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
    void shouldFindBySkuDepartmentAndProductBrand() throws IOException, ExecutionException, InterruptedException {

        // given
        List<ProductDocumentDto> productDataSet = List.of(
                ProductDocumentTestBuilder.builder()
                        .withBrand("Nike")
                        .buildDto(),
                ProductDocumentTestBuilder.builder()
                        .withBrand("Nike")
                        .withId(UUID.fromString("7286161e-80bd-4a9f-9d37-3aa684193706"))
                        .buildDto(),
                ProductDocumentTestBuilder.builder()
                        .withBrand("Adidas")
                        .withId(UUID.fromString("e25b3a44-b927-43dc-92ac-5aa670b6e8d1"))
                        .buildDto()
        );

        List<SkuDocumentDto> skuDataSet = List.of(
                SkuDocumentTestBuilder.builder()
                        .withDepartment("dep1")
                        .withProductId(productDataSet.getFirst().id())
                        .buildDto(),
                SkuDocumentTestBuilder.builder()
                        .withDepartment("dep2")
                        .withId(UUID.fromString("d3893f80-e484-4458-8ce5-83e0686e953a"))
                        .withProductId(productDataSet.getFirst().id())
                        .buildDto(),
                SkuDocumentTestBuilder.builder()
                        .withDepartment("dep1")
                        .withId(UUID.fromString("3ddb7eef-c5b8-42e4-bb06-eb80eea8e45b"))
                        .withProductId(productDataSet.getLast().id())
                        .buildDto()
        );

        skuElasticsearchService.indexSkuDocumentDtosInBulk(skuDataSet).get();
        serviceImpl.indexProductDocumentDtosInBulk(productDataSet).get();

        esClient.indices().refresh();

        // when
        PagedResponse<ProductResponse> found = serviceImpl.findAllByHavingChildWithSkuDepartmentAndProductBrand("Nike", "dep1", 0, 10);

        // then
        assertThat(found.content()).hasSize(1);
        assertThat(found.content().getFirst()).usingRecursiveComparison()
                .isEqualTo(productDataSet.getFirst());
    }

    @Test
    void shouldFindBySkuDepartmentAndProductBrandWithProperPagingAndSorting() throws IOException, ExecutionException, InterruptedException {

        // given
        List<ProductDocumentDto> productDataSet = List.of(
                ProductDocumentTestBuilder.builder()
                        .withBrand("Nike")
                        .withCreatedAt("2021-07-01T11:11:11")
                        .buildDto(),
                ProductDocumentTestBuilder.builder()
                        .withBrand("Nike")
                        .withId(UUID.fromString("7286161e-80bd-4a9f-9d37-3aa684193706"))
                        .withCreatedAt("2021-07-01T11:11:12")
                        .buildDto(),
                ProductDocumentTestBuilder.builder()
                        .withBrand("Nike")
                        .withId(UUID.fromString("e25b3a44-b927-43dc-92ac-5aa670b6e8d1"))
                        .withCreatedAt("2021-07-01T11:11:13")
                        .buildDto()
        );

        List<SkuDocumentDto> skuDataSet = List.of(
                SkuDocumentTestBuilder.builder()
                        .withDepartment("dep1")
                        .withProductId(productDataSet.getFirst().id())
                        .buildDto(),
                SkuDocumentTestBuilder.builder()
                        .withDepartment("dep1")
                        .withId(UUID.fromString("d3893f80-e484-4458-8ce5-83e0686e953a"))
                        .withProductId(productDataSet.get(1).id())
                        .buildDto(),
                SkuDocumentTestBuilder.builder()
                        .withDepartment("dep1")
                        .withId(UUID.fromString("3ddb7eef-c5b8-42e4-bb06-eb80eea8e45b"))
                        .withProductId(productDataSet.getLast().id())
                        .buildDto()
        );

        skuElasticsearchService.indexSkuDocumentDtosInBulk(skuDataSet).get();
        serviceImpl.indexProductDocumentDtosInBulk(productDataSet).get();

        esClient.indices().refresh();

        // when
        PagedResponse<ProductResponse> foundFirstPage = serviceImpl.findAllByHavingChildWithSkuDepartmentAndProductBrand("Nike", "dep1", 0, 2);
        PagedResponse<ProductResponse> foundSecondPage = serviceImpl.findAllByHavingChildWithSkuDepartmentAndProductBrand("Nike", "dep1", 2, 2);

        // then
        assertThat(foundFirstPage.content()).hasSize(2);
        assertThat(foundFirstPage.content().stream()
                .map(ProductElasticsearchServiceImplTest::mapFromProductResponse)
                .toList()
        ).containsExactly(productDataSet.get(2), productDataSet.get(1));

        assertThat(foundSecondPage.content()).hasSize(1);
        assertThat(mapFromProductResponse(foundSecondPage.content().getFirst()))
                .isEqualTo(productDataSet.getFirst());
    }


    @Test
    void shouldFindByProductTitleAndProductDescription() throws IOException, ExecutionException, InterruptedException {

        // given
        List<ProductDocumentDto> productDataSet = List.of(
                ProductDocumentTestBuilder.builder()
                        .withTitle("TITLE_ONE")
                        .withDescription("DESC_ONE")
                        .buildDto(),
                ProductDocumentTestBuilder.builder()
                        .withTitle("TITLE_TWO")
                        .withDescription("DESC_TWO")
                        .withId(UUID.fromString("7286161e-80bd-4a9f-9d37-3aa684193706"))
                        .buildDto(),
                ProductDocumentTestBuilder.builder()
                        .withTitle("TITLE_ONE")
                        .withDescription("DESC_THREE")
                        .withId(UUID.fromString("e25b3a44-b927-43dc-92ac-5aa670b6e8d1"))
                        .buildDto()
        );

        serviceImpl.indexProductDocumentDtosInBulk(productDataSet).get();

        esClient.indices().refresh();

        // when
        PagedResponse<ProductResponse> found = serviceImpl.findAllByProductTitleOrProductDescription("TITLE_ONE", "DESC_ONE", 0, 10);

        // then
        assertThat(found.content()).hasSize(2);
        assertThat(found.content().stream()
                .map(ProductElasticsearchServiceImplTest::mapFromProductResponse)
                .toList()
        ).containsExactly(productDataSet.get(0), productDataSet.get(2));
    }
}