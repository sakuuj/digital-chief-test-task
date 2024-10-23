package by.sakuuj.digital.chief.testproj.service;

import by.sakuuj.digital.chief.testproj.dto.ProductDocumentDto;
import by.sakuuj.digital.chief.testproj.dto.ProductResponse;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductElasticsearchService {

    CompletableFuture<Void> indexProductDocumentDtosInBulk(List<ProductDocumentDto> products);

    PagedResponse<ProductResponse> findAllByHavingChildWithSkuDepartmentAndProductBrand(String productBrand,
                                                                                        String childSkuDepartment,
                                                                                        int from,
                                                                                        int size);

    PagedResponse<ProductResponse> findAllByProductTitleOrProductDescription(String productTitle,
                                                                             String productDescription,
                                                                             int from,
                                                                             int size);
}
