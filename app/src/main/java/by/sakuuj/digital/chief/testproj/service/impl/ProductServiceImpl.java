package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.dto.ProductRequest;
import by.sakuuj.digital.chief.testproj.dto.ProductResponse;
import by.sakuuj.digital.chief.testproj.entity.Product;
import by.sakuuj.digital.chief.testproj.exception.EntityNotFoundException;
import by.sakuuj.digital.chief.testproj.exception.NotMatchingEntityVersionException;
import by.sakuuj.digital.chief.testproj.mapper.ProductMapper;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;
import by.sakuuj.digital.chief.testproj.repository.ProductRepository;
import by.sakuuj.digital.chief.testproj.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> findAllSortedByCreatedAtDesc(Pageable pageable) {

        Page<Product> foundPage = productRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("modificationAudit.createdAt").descending()
        ));

        return foundPage.get()
                .map(productMapper::toResponse)
                .collect(Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> PagedResponse.<ProductResponse>builder()
                                        .content(list)
                                        .pageSize(pageable.getPageSize())
                                        .pageNumber(pageable.getPageNumber())
                                        .totalElementsCount((int)foundPage.getTotalElements())
                                        .build()
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> findById(UUID id) {

        return productRepository.findById(id).map(productMapper::toResponse);
    }

    @Override
    public void deleteById(UUID id) {

        productRepository.deleteById(id);
    }

    @Override
    public void update(UUID id, short version, ProductRequest request) {

        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id '%s' has not been found"
                        .formatted(id.toString()))
                );

        if (productToUpdate.getVersion() != version) {

            throw new NotMatchingEntityVersionException("Provided version: %d, actual version: %d"
                    .formatted(version, productToUpdate.getVersion()));
        }

        productMapper.update(productToUpdate, request);
    }

    @Override
    public UUID save(ProductRequest productRequest) {

        Product productToSave = productMapper.toEntity(productRequest);
        Product saved = productRepository.save(productToSave);

        return saved.getId();
    }
}
