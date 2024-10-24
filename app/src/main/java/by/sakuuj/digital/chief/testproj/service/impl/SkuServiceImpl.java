package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.dto.SkuRequest;
import by.sakuuj.digital.chief.testproj.dto.SkuResponse;
import by.sakuuj.digital.chief.testproj.entity.Sku;
import by.sakuuj.digital.chief.testproj.exception.EntityNotFoundException;
import by.sakuuj.digital.chief.testproj.exception.NotMatchingEntityVersionException;
import by.sakuuj.digital.chief.testproj.mapper.SkuMapper;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;
import by.sakuuj.digital.chief.testproj.repository.SkuRepository;
import by.sakuuj.digital.chief.testproj.service.SkuService;
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
public class SkuServiceImpl implements SkuService {

    private final SkuRepository skuRepository;
    private final SkuMapper skuMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SkuResponse> findAllSortedByCreatedAtDesc(Pageable pageable) {

        Page<Sku> foundPage = skuRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("modificationAudit.createdAt").descending()
        ));

        return foundPage.get()
                .map(skuMapper::toResponse)
                .collect(Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> PagedResponse.<SkuResponse>builder()
                                        .content(list)
                                        .pageSize(pageable.getPageSize())
                                        .pageNumber(pageable.getPageNumber())
                                        .totalCount(foundPage.getTotalPages())
                                        .build()
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SkuResponse> findById(UUID id) {

        return skuRepository.findById(id).map(skuMapper::toResponse);
    }

    @Override
    public void deleteById(UUID id) {

        skuRepository.deleteById(id);
    }

    @Override
    public void update(UUID id, short version, SkuRequest request) {

        Sku skuToUpdate = skuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id '%s' has not been found"
                        .formatted(id.toString()))
                );

        if (skuToUpdate.getVersion() != version) {

            throw new NotMatchingEntityVersionException("Provided version: %d, actual version: %d"
                    .formatted(version, skuToUpdate.getVersion()));
        }

        skuMapper.update(skuToUpdate, request);
    }

    @Override
    public UUID save(SkuRequest request) {

        Sku skuToSave = skuMapper.toEntity(request);
        Sku saved = skuRepository.save(skuToSave);

        return saved.getSequenceNumber();
    }
}
