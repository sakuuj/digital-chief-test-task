package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.mapper.ProductMapper;
import by.sakuuj.digital.chief.testproj.mapper.SkuMapper;
import by.sakuuj.digital.chief.testproj.repository.ProductRepository;
import by.sakuuj.digital.chief.testproj.repository.SkuRepository;
import by.sakuuj.digital.chief.testproj.service.DbToElasticDataTransferService;
import by.sakuuj.digital.chief.testproj.service.IndexCreationService;
import by.sakuuj.digital.chief.testproj.service.ProductElasticsearchService;
import by.sakuuj.digital.chief.testproj.service.SkuElasticsearchService;
import by.sakuuj.digital.chief.testproj.utils.FutureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbToElasticDataTransferServiceImpl implements DbToElasticDataTransferService {

    private final IndexCreationService indexCreationService;

    private final ProductElasticsearchService productElasticsearchService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final SkuElasticsearchService skuElasticsearchService;
    private final SkuRepository skuRepository;
    private final SkuMapper skuMapper;

    @Override
    public void createIndex(String indexName, String jsonFileName) {
        indexCreationService.createIndex(indexName, jsonFileName);
    }

    @Override
    public void transferProductsCreatedAtAfterPointInTime(LocalDateTime pointInTime) {

        transferEntitiesCreatedAtAfterPointInTime(
                pointInTime,
                productRepository::findAllByModificationAudit_CreatedAtAfter,
                productMapper::toDocumentDto,
                productElasticsearchService::indexProductDocumentDtosInBulk
        );
    }

    @Override
    public void transferSkusCreatedAtAfterPointInTime(LocalDateTime pointInTime) {

        transferEntitiesCreatedAtAfterPointInTime(
                pointInTime,
                skuRepository::findAllByModificationAudit_CreatedAtAfter,
                skuMapper::toDocumentDto,
                skuElasticsearchService::indexSkuDocumentDtosInBulk
        );
    }


    private <T, U> void transferEntitiesCreatedAtAfterPointInTime(
            LocalDateTime pointInTime,
            BiFunction<LocalDateTime, Pageable, Slice<T>> findEntitiesLambda,
            Function<T, U> entityToDocumentDtoMapper,
            Function<List<U>, CompletableFuture<Void>> indexDocumentDtosLambda
    ) {

        log.info("Starting data transfer process...");
        int pageSize = 50;
        int pageNum = 0;
        CompletableFuture<Void> indexEntitiesFuture = null;
        Slice<T> foundEntitiesSlice;
        do {
            Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("modificationAudit.createdAt").ascending());
            foundEntitiesSlice = findEntitiesLambda.apply(pointInTime, pageable);

            log.info("Loaded page #{} with actual size {}", pageNum, foundEntitiesSlice.getNumberOfElements());

            if (indexEntitiesFuture != null) {

                FutureUtils.waitForCompletion(indexEntitiesFuture);

                log.info("Page #{} was transferred", pageNum - 1);
            }

            List<U> documentsToIndex = foundEntitiesSlice.getContent()
                    .stream()
                    .map(entityToDocumentDtoMapper)
                    .toList();

            log.info("Transferring page #{}", pageNum);

            indexEntitiesFuture =  indexDocumentDtosLambda.apply(documentsToIndex);

            pageNum++;

        } while (foundEntitiesSlice.hasNext());

        if (indexEntitiesFuture != null) {
            FutureUtils.waitForCompletion(indexEntitiesFuture);

            log.info("Page #{} was transferred", pageNum - 1);
        }
    }
}
