package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.dto.ProductDocumentDto;
import by.sakuuj.digital.chief.testproj.dto.ProductResponse;
import by.sakuuj.digital.chief.testproj.mapper.ProductMapper;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;
import by.sakuuj.digital.chief.testproj.service.GenericElasticsearchService;
import by.sakuuj.digital.chief.testproj.service.ProductElasticsearchService;
import by.sakuuj.digital.chief.testproj.utils.JsonExtractorUtils;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
public class ProductElasticsearchServiceImpl implements ProductElasticsearchService {

    private final ProductMapper productMapper;
    private final GenericElasticsearchService genericEsService;
    private final String mainIndexName;

    private static final String TEMPLATE_SKU_DEPARTMENT_AND_PRODUCT_BRAND_QUERY = JsonExtractorUtils
            .fromFile("/elasticsearch/query/sku_department__product_brand.json");

    private static final String TEMPLATE_PRODUCT_TITLE_OR_PRODUCT_DESCRIPTION_QUERY = JsonExtractorUtils
            .fromFile("/elasticsearch/query/product_title__product_description.json");

    public ProductElasticsearchServiceImpl(
            ProductMapper productMapper,
            GenericElasticsearchServiceImpl genericEsService,
            @Value("${by.sakuuj.elasticsearch.main-index.name}")
            String mainIndexName
    ) {
        this.productMapper = productMapper;
        this.genericEsService = genericEsService;
        this.mainIndexName = mainIndexName;
    }

    public CompletableFuture<Void> indexProductDocumentDtosInBulk(List<ProductDocumentDto> products) {

        return genericEsService.indexInBulk(products, p -> p.id().toString(), mainIndexName);
    }

    public <T, R> PagedResponse<R> findAllByJsonQueryWithTotalCount(
            String jsonQuery,
            int from,
            int size,
            Function<Hit<T>, R> mapper,
            Class<T> clazz
    ) {
        HitsMetadata<T> hits = genericEsService.findWithJson(
                mainIndexName,
                jsonQuery,
                clazz
        );

        int totalElementsCount = (int) Objects.requireNonNull(hits.total()).value();
        List<R> responseContent = hits.hits().stream()
                .map(mapper)
                .toList();

        return PagedResponse.<R>builder()
                .content(responseContent)
                .totalElementsCount(totalElementsCount)
                .pageSize(size)
                .pageNumber(from)
                .build();
    }

    public PagedResponse<ProductResponse> findAllByProductTitleOrProductDescription(
            String productTitle,
            String productDescription,
            int from,
            int size
    ) {
        String jsonQuery = TEMPLATE_PRODUCT_TITLE_OR_PRODUCT_DESCRIPTION_QUERY
                .replace("?0", productTitle)
                .replace("?1", productDescription)
                .replace(":from", String.valueOf(from))
                .replace(":size", String.valueOf(size));

        return findAllByJsonQueryWithTotalCount(
                jsonQuery,
                from,
                size,
                fromHitToProductResponseMapper(),
                ProductDocumentDto.class
        );
    }

    public PagedResponse<ProductResponse> findAllByHavingChildWithSkuDepartmentAndProductBrand(
            String productBrand,
            String childSkuDepartment,
            int from,
            int size
    ) {
        String jsonQuery = TEMPLATE_SKU_DEPARTMENT_AND_PRODUCT_BRAND_QUERY
                .replace("?0", childSkuDepartment)
                .replace("?1", productBrand)
                .replace(":from", String.valueOf(from))
                .replace(":size", String.valueOf(size));

        return findAllByJsonQueryWithTotalCount(
                jsonQuery,
                from,
                size,
                fromHitToProductResponseMapper(),
                ProductDocumentDto.class
        );
    }

    private Function<Hit<ProductDocumentDto>, ProductResponse> fromHitToProductResponseMapper() {
        return hit -> {
            String id = hit.id();
            Objects.requireNonNull(id);

            ProductDocumentDto source = hit.source();
            Objects.requireNonNull(source);

            ProductDocumentDto productDocumentDto = source.withId(UUID.fromString(id));

            return productMapper.toResponse(productDocumentDto);
        };
    }
}
