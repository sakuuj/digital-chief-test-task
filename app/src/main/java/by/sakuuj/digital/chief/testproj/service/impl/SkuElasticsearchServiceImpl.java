package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.dto.SkuDocumentDto;
import by.sakuuj.digital.chief.testproj.entity.Sku;
import by.sakuuj.digital.chief.testproj.service.SkuElasticsearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SkuElasticsearchServiceImpl implements SkuElasticsearchService {

    private final GenericElasticsearchServiceImpl genericEsService;
    private final String mainIndexName;

    public SkuElasticsearchServiceImpl(GenericElasticsearchServiceImpl genericEsService,
                                       @Value("${by.sakuuj.elasticsearch.main-index.name}") String mainIndexName) {
        this.genericEsService = genericEsService;
        this.mainIndexName = mainIndexName;
    }

    @Override
    public CompletableFuture<Void> indexSkuDocumentDtosInBulk(List<SkuDocumentDto> skus) {
        return genericEsService.indexInBulk(skus, sku -> sku.getId().toString(), mainIndexName);
    }
}
