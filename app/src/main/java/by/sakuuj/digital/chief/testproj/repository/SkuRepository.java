package by.sakuuj.digital.chief.testproj.repository;

import by.sakuuj.digital.chief.testproj.entity.Product;
import by.sakuuj.digital.chief.testproj.entity.Sku;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SkuRepository extends JpaRepository<Sku, UUID> {

    Slice<Sku> findAllWhereCreatedAtAfterPointInTime(LocalDateTime pointInTime, Pageable pageable);
}
