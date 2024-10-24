package by.sakuuj.digital.chief.testproj.service;

import co.elastic.clients.elasticsearch.core.search.HitsMetadata;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface GenericElasticsearchService {

    <T> HitsMetadata<T> findWithJson(String indexName, String json, Class<T> clazz);

    <T> CompletableFuture<Void> indexInBulk(List<? extends T> documents, Function<? super T, String> idExtractor, String indexName);
}
