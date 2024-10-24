package by.sakuuj.digital.chief.testproj.repository;

import by.sakuuj.digital.chief.testproj.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Slice<Product> findAllByModificationAudit_CreatedAtAfter(LocalDateTime pointInTime, Pageable pageable);
}
