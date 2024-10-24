package by.sakuuj.digital.chief.testproj.service;

import by.sakuuj.digital.chief.testproj.dto.SkuDocumentDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SkuElasticsearchService {

    CompletableFuture<Void> indexSkuDocumentDtosInBulk(List<SkuDocumentDto> skus);
}
