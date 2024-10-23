package by.sakuuj.digital.chief.testproj.service;

import by.sakuuj.digital.chief.testproj.dto.ProductRequest;
import by.sakuuj.digital.chief.testproj.dto.ProductResponse;
import by.sakuuj.digital.chief.testproj.dto.SkuRequest;
import by.sakuuj.digital.chief.testproj.dto.SkuResponse;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SkuService {

    PagedResponse<SkuResponse> findAllSortedByCreatedAtDesc(Pageable pageable);

    Optional<SkuResponse> findById(UUID id);

    UUID save(SkuRequest skuRequest);

    void update(UUID id, short version, SkuRequest request);

    void deleteById(UUID id);
}
