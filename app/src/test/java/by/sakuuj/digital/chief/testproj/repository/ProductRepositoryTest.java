package by.sakuuj.digital.chief.testproj.repository;

import by.sakuuj.digital.chief.testproj.ProductTestBuilder;
import by.sakuuj.digital.chief.testproj.entity.ModificationAudit;
import by.sakuuj.digital.chief.testproj.entity.Product;
import by.sakuuj.digital.chief.testproj.testcontainers.PostgresSingletonContainerLauncher;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest extends PostgresSingletonContainerLauncher {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    void shouldFindProductsCreatedAtAfterPointInTime() {

        // given
        entityManager.createQuery("delete Sku s").executeUpdate();
        entityManager.createQuery("delete Product p").executeUpdate();
        entityManager.flush();

        ProductTestBuilder productBuilder = ProductTestBuilder.builder()
                .withId(null)
                .withVersion((short) 0);

        Product firstProduct = productBuilder.build();
        Product secondProduct = productBuilder.build();
        Product thirdProduct = productBuilder.build();

        entityManager.persist(firstProduct);
        entityManager.flush();
        entityManager.persist(secondProduct);
        entityManager.flush();
        entityManager.persist(thirdProduct);
        entityManager.flush();

        // when
        Slice<Product> found = productRepository.findAllByModificationAudit_CreatedAtAfter(
                firstProduct.getModificationAudit().getCreatedAt(),
                PageRequest.of(0, 10, Sort.by("modificationAudit.createdAt").descending())
        );

        // then
        assertThat(found).hasSize(2).containsExactly(thirdProduct, secondProduct);
    }
}