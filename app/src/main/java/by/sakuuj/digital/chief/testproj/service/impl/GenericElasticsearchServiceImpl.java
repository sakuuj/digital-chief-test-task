package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.exception.BulkIndexingException;
import by.sakuuj.digital.chief.testproj.service.GenericElasticsearchService;
import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericElasticsearchServiceImpl implements GenericElasticsearchService {

    private final ElasticsearchAsyncClient esAsyncClient;
    private final ElasticsearchClient esClient;

    private static final String SINGLE_NODE_ROUTING_VALUE = "1";

    @Override
    public <T> HitsMetadata<T>  findWithJson(String indexName, String json, Class<T> clazz) {
        try {
            Reader input = new StringReader(json);

            SearchResponse<T> searchResponse = esClient.search(b0 -> b0.index(indexName).withJson(input), clazz);

            return searchResponse.hits();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> CompletableFuture<Void> indexInBulk(List<? extends T> documents, Function<? super T, String> idExtractor, String indexName) {

        BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();

        if (documents.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        documents.forEach(d ->
                bulkBuilder.operations(op -> op
                        .index(b0 -> b0
                                .index(indexName)
                                .id(idExtractor.apply(d))
                                .document(d)
                                .routing(SINGLE_NODE_ROUTING_VALUE)
                        )
                )
        );


        CompletableFuture<BulkResponse> bulkResponse = esAsyncClient.bulk(bulkBuilder.build());

        return bulkResponse.thenApply(bulkResp -> {

            if (!bulkResp.errors()) {
                return null;
            }

            BulkResponseItem firstErrorResponseItem = null;
            int errorCount = 0;

            log.error("Bulk indexing had errors");
            for (BulkResponseItem item : bulkResp.items()) {
                if (item.error() == null) {
                    continue;
                }

                if (firstErrorResponseItem == null) {
                    firstErrorResponseItem = item;
                }

                errorCount++;
                log.error("Bulk indexing error #{}: {}", errorCount, item.error().reason());
            }

            throw new BulkIndexingException("Bulk indexing failure: %d errors, first error response item: %s".formatted(
                    errorCount,
                    firstErrorResponseItem)
            );
        });
    }

}
