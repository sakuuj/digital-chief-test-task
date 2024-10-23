package by.sakuuj.digital.chief.testproj.service;

import by.sakuuj.digital.chief.testproj.dto.ProductRequest;
import by.sakuuj.digital.chief.testproj.dto.ProductResponse;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    PagedResponse<ProductResponse> findAllSortedByCreatedAtDesc(Pageable pageable);

    Optional<ProductResponse> findById(UUID id);

    UUID save(ProductRequest productRequest);

    void update(UUID id, short version, ProductRequest request);

    void deleteById(UUID id);

}
